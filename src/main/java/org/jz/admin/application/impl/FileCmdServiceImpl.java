package org.jz.admin.application.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jz.admin.application.FileCmdService;

import org.jz.admin.dto.*;
import org.jz.admin.dto.co.FileCheckUpProgressCO;
import org.jz.admin.common.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.infrastructure.db.convertor.FileConvertor;

import org.jz.admin.dto.co.FileMoveOrCopyCo;
import org.jz.admin.domain.valueobject.Description;
import org.jz.admin.domain.File;
import org.jz.admin.domain.FileResource;
import org.jz.admin.infrastructure.db.dataobject.FileDO;
import org.jz.admin.infrastructure.db.repository.FileRepositoryImpl;
import org.jz.admin.infrastructure.db.repository.FileResourceRepositoryImpl;
import org.jz.admin.common.enums.FileStatusEnum;
import org.jz.admin.infrastructure.db.dataobject.FileResourceDO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author jz
 * @date 2020/07/14
 */
@Service
public class FileCmdServiceImpl implements FileCmdService {

    @Autowired
    private FileResourceRepositoryImpl fileResourceRepository;

    @Autowired
    private FileRepositoryImpl fileRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private HttpServletResponse response;

    @Override
    public Response delete(FileDeleteCmd cmd) {
        List<File> filesByRelativePaths = fileRepository.getFilesByRelativePaths(cmd.getRelativePaths());
        fileRepository.batchDeleteByRelativePath(filesByRelativePaths);
        return Response.ok();
    }


    @Override
    public Response makeDir(FileMakeDirCmd cmd) {
        File folder = new File().setDescription(new Description(cmd.getRelativePath(), true));
        fileRepository.createDir(folder, false);
        return Response.ok();
    }

    @Override
    public Response rename(FileRenameCmd cmd) {
        File file = fileRepository.getFileByRelativePath(cmd.getRelativePath());
        if (file == null) {
            return Response.failed();
        }

        Description originFileDescription = file.getDescription();
        file.rename(cmd.getTargetName());
        if (fileRepository.getFileByRelativePath(file.getRelativePath()) != null) {
            file.toNewFileName();
        }
        if (!file.isFolder()) {
            fileRepository.saveOrUpdate(file);
        } else {
            List<File> filesWithSubFiles =
                    fileRepository.getFilesWithSubFilesByRelativePath(Collections.singletonList(new File().setDescription(originFileDescription)));
            for (File filesWithSubFile : filesWithSubFiles) {
                String suf = StrUtil.removePrefix(filesWithSubFile.getRelativePath(),
                        originFileDescription.getRelativePath());
                filesWithSubFile.setDescription(new Description(file.getRelativePath()));
            }
            fileRepository.saveOrUpdateBatch(filesWithSubFiles);
        }
        return Response.ok();
    }

    @Override
    public Response checkUpProgress(FileCheckUpProgressCmd cmd) {

        FileResource resource =
                new FileResource().setIdentifier(cmd.getIdentifier()).setFileName(cmd.getFilename()).setStatus(FileStatusEnum.NEW);

        FileResourceDO fileStoreDO = fileResourceRepository.getResourceIdByIdentifier(cmd.getIdentifier());
        if (fileStoreDO != null) {
            resource.setId(fileStoreDO.getId()).setStatus(fileStoreDO.getStatus());
        }

        FileCheckUpProgressCO progressResponse = new FileCheckUpProgressCO();

        if (resource.isCreated()) {
            return Response.ok(progressResponse.setId(resource.getId()).setSkipUpload(true));
        }

        if (resource.getId() != null && !resource.isCreated()) {
            Set<Integer> members = redisTemplate.opsForSet().members(cmd.getIdentifier());
            return Response.ok(progressResponse.setId(resource.getId()).setSkipUpload(false).setUploaded(members));
        }

        resource.generateRealPath();
        if (!fileResourceRepository.save(resource)) {
            throw new ServiceException("保存失败");
        }

        return Response.ok(progressResponse.setId(resource.getId()).setSkipUpload(false));
    }


    @Override
    public Response chunkUpload(MultipartFile file, FileChunkUploadCmd cmd) {
        FileResource resource = new FileResource()
                .setIdentifier(cmd.getIdentifier())
                .setChunkNumber(cmd.getChunkNumber())
                .setChunkSize(cmd.getChunkSize());
        FileResourceDO fileStoreDO = fileResourceRepository.getResourceIdByIdentifier(cmd.getIdentifier());
        if (fileStoreDO == null) {
            throw new ServiceException("上传文件失败,源文件缺失");
        }
        resource.setId(fileStoreDO.getId()).setPath(fileStoreDO.getPath()).setStatus(fileStoreDO.getStatus());
        if (BooleanUtil.isFalse(redisTemplate.opsForSet().isMember(cmd.getIdentifier(), cmd.getChunkNumber()))) {
            resource.rangeWrite(file);
            FileStatusEnum status = resource.readyToUpdateStatus();
            if (!status.equals(resource.getStatus())) {
                //需要更新状态
                fileResourceRepository.save(new FileResource().setId(resource.getId()).setStatus(status));
            }
            //添加缓存
            redisTemplate.opsForSet().add(cmd.getIdentifier(), cmd.getChunkNumber());
        }
        return Response.ok();
    }


    @Override
    public Response merge(FileMergeCmd cmd) {
        File file = new File()
                .setDescription(new Description(cmd.getRelativePath(), false))
                .setSize(cmd.getTotalSize())
                .setResourceId(cmd.getStorageId())
                .setSize(cmd.getTotalSize())
                .setStatus(FileStatusEnum.CREATED);
        // 判断是否有重名文件
        if (fileRepository.getFileByRelativePath(file.getRelativePath()) != null) {
            file.toNewFileName();
        }

        RLock fileLock = redissonClient.getLock("fileLock");
        final File parentFolder;
        try {
            fileLock.lock();
            parentFolder = fileRepository.createDir(file.newParentFolder(), true);
        } finally {
            fileLock.unlock();
        }
        file.setFolderId(parentFolder.getId());
        if (!fileRepository.saveOrUpdate(file)) {
            throw new ServiceException("创建文件失败");
        }
        return Response.ok(file);
    }

    @Override
    public Response copy(FileMoveOrCopyCmd cmd) {
        moveOrCopy(cmd, false);
        return Response.ok();
    }

    @Override
    public Response move(FileMoveOrCopyCmd cmd) {
        moveOrCopy(cmd, true);
        return Response.ok();
    }

    private void moveOrCopy(FileMoveOrCopyCmd cmd, boolean move) {
        Map<String, List<String>> targetPathMap =
                cmd.getFileList().stream().collect(Collectors.groupingBy(FileMoveOrCopyCo::getTargetPath,
                        Collectors.mapping(FileMoveOrCopyCo::getRelativePath, Collectors.toList())));

        for (Map.Entry<String, List<String>> entry : targetPathMap.entrySet()) {
            File targetFolder = new File().setDescription(new Description(entry.getKey(), true));
            if (targetFolder.getId() == null) {
                targetFolder = fileRepository.getFileByRelativePath(targetFolder.getRelativePath());
            }
            if (targetFolder == null || !targetFolder.isFolder()) {
                throw new ServiceException("找不到目标目录");
            }

            List<File> moveFiles = fileRepository.getFilesByRelativePaths(entry.getValue());
            for (File moveFile : moveFiles) {
                if (moveFile.isSameOrParentDir(targetFolder)) {
                    throw new ServiceException("不能复制/移动到自身目录或子目录");
                }
            }

            List<File> filesWithSubFiles = fileRepository.getFilesWithSubFilesByRelativePath(moveFiles);

            for (File moveFile : moveFiles) {
                File targetFile = targetFolder.newSubFile(moveFile.getDescription().getFileName(), moveFile.isFolder());
                if (fileRepository.getFileByRelativePath(targetFile.getRelativePath()) != null) {
                    targetFile.toNewFileName();
                }
                for (File fileReadyToUpdate : filesWithSubFiles) {
                    if (fileReadyToUpdate.getId().equals(moveFile.getId())) {
                        fileReadyToUpdate.setDescription(targetFile.getDescription());
                        fileReadyToUpdate.setFolderId(targetFolder.getId());
                    } else if (moveFile.isParentDir(fileReadyToUpdate)) {
                        String newRelativePath =
                                targetFile.getRelativePath() + StrUtil.removePrefix(fileReadyToUpdate.getRelativePath(), moveFile.getRelativePath());
                        fileReadyToUpdate.setDescription(new Description(newRelativePath));
                    }
                }
            }
            if (!move) {
                filesWithSubFiles.forEach(f -> f.setId(null));
            }
            fileRepository.saveOrUpdateBatch(filesWithSubFiles);
        }
    }

    @Override
    public void download(FileDownloadCmd cmd) {
        List<File> files =
                fileRepository.listByIds(cmd.getFidList()).stream().map(FileConvertor::deserialize).collect(Collectors.toList());
        if (CollUtil.isEmpty(files)) {
            throw new ServiceException("下载文件不存在");
        }
        File first = CollUtil.getFirst(files);

        final String downloadFileName;
        if (files.size() == 1 && !first.isFolder()) {
            downloadFileName = first.getDescription().getFileName();
        } else {
            downloadFileName = IdUtil.fastSimpleUUID() + StrUtil.DOT + "zip";
        }
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, URLUtil.encode(downloadFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        try {
            if (files.size() == 1 && !first.isFolder()) {
                BufferedInputStream inputStream =
                        FileUtil.getInputStream(fileResourceRepository.getById(first.getResourceId()).getPath());
                IoUtil.copy(inputStream, response.getOutputStream());
            } else {
                String commonPrefix = commonPath(files);
                List<File> fileListWithRealPath = fileRepository.getFileListWithSourcePath(files);
                ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream());
                zip(outputStream, fileListWithRealPath, commonPrefix);
                outputStream.close();
            }
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private void zip(ZipOutputStream outputStream, List<File> fileListWithRealPath, String commonPrefix) throws IOException {
        for (File file : fileListWithRealPath) {
            if (!file.isFolder()) {
                if (file.getPath() != null) {
                    outputStream.putNextEntry(new ZipEntry(StrUtil.removePrefix(file.getRelativePath(),
                            commonPrefix)));
                    BufferedInputStream inputStream = FileUtil.getInputStream(file.getPath());
                    IoUtil.copy(inputStream, outputStream);
                    inputStream.close();
                }
            } else {
                outputStream.putNextEntry(new ZipEntry(StrUtil.removePrefix(file.getRelativePath() + StrUtil.SLASH,
                        commonPrefix)));
            }
            outputStream.closeEntry();
        }
    }

    //获取公共路径
    private String commonPath(List<File> files) {
        if (CollUtil.isEmpty(files)) {
            return StrUtil.EMPTY;
        }
        File first = CollUtil.getFirst(files);
        File parent = first.newParentFolder();
        while (parent.getId() == null) {
            boolean isPre = true;
            for (File file : files) {
                if (!parent.isParentDir(file)) {
                    isPre = false;
                    break;
                }
            }
            if (isPre) {
                return parent.getRelativePath();
            }
            parent = parent.newParentFolder();
        }
        return StrUtil.EMPTY;
    }

    @Override
    public Response list(FileListQry qry) {
        File parentFolder = new File().setDescription(new Description(qry.getRelativePath(), true));
        if (parentFolder.getId() == null) {
            File fileByRelativePath = fileRepository.getFileByRelativePath(qry.getRelativePath());
            if (fileByRelativePath != null) {
                parentFolder.setId(fileByRelativePath.getId());
            }
        }
        Page<FileDO> filePage = fileRepository.filePage(parentFolder.getId(), qry.getType(),
                qry.getOrder(), qry.getAsc(), qry.getPage(), qry.getPageSize());
        return Response.ok(filePage);
    }
}
