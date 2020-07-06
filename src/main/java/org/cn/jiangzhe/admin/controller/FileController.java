package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.aspect.CommonLog;
import org.cn.jiangzhe.admin.aspect.ServiceException;
import org.cn.jiangzhe.admin.entity.FileStatusEnum;
import org.cn.jiangzhe.admin.entity.FileTypeEnum;
import org.cn.jiangzhe.admin.entity.TFieStorage;
import org.cn.jiangzhe.admin.entity.TFile;
import org.cn.jiangzhe.admin.mapper.FieStorageMapper;
import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.cn.jiangzhe.admin.service.FileServiceImpl;
import org.cn.jiangzhe.admin.service.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 作为一个文件大致有三种状态：生成、完成、删除
 *
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@CommonLog
@RestController
public class FileController {

    Map<String, Set<Integer>> map = new ConcurrentHashMap<>();

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    FileUtilService fileUtilService;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    FieStorageMapper fileStoreMapper;

    public static String DEMO_DIR = "public/";
    public static final long TOP_FOLDER_ID = 0L;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Params {
        private String filename;
        private String relativePath;
        private Integer chunkNumber;
        private Integer chunkSize;
        private Integer currentChunkSize;
        private Long totalSize;
        private Long storageId;
        private String identifier;
        private Integer totalChunks;
        private List<String> relativePaths;
        private String targetName;
        private List<FileVO> fileList;
        private FileTypeEnum type;

    }

    @Data
    private static class FileVO {
        private String relativePath;
        private String targetPath;
        private String targetName;
    }

    @PostMapping("listFile")
    public Object listFile(@RequestBody Params params) {
        Long folderId = null;

        String relativePath = params.getRelativePath();
        if (StrUtil.isBlank(relativePath)) {
            folderId = TOP_FOLDER_ID;
        }

        if (folderId == null) {
            // 通过相对路径找到id
            TFile folder = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                    .select(TFile::getId)
                    .eq(TFile::getStatus, FileStatusEnum.CREATED)
                    .eq(TFile::getRelativePath, relativePath));
            if (folder == null) {
                return Collections.emptyList();
            }
            folderId = folder.getId();
        }

        return fileMapper.selectList(new LambdaQueryWrapper<TFile>()
                .select(TFile::getId, TFile::getOriginalFileName, TFile::getUpdateTime, TFile::getType,
                        TFile::getSize, TFile::getRelativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(params.getType() != null, TFile::getType, params.getType())
                .eq(TFile::getFolderId, folderId)
                .orderByDesc(TFile::getUpdateTime)
                .orderByAsc(TFile::getType)
        );

    }

    @PostMapping("deleteFile")
    public Object deleteFile(@RequestBody Params params) {
        List<String> relativePaths = params.getRelativePaths();
        for (String relativePath : relativePaths) {
            int update = fileMapper.update(null, new LambdaUpdateWrapper<TFile>()
                    .set(TFile::getStatus, FileStatusEnum.DELETED)
                    .eq(TFile::getStatus, FileStatusEnum.CREATED)
                    .and(wrapper -> wrapper
                            .eq(TFile::getRelativePath, relativePath)
                            .or()
                            .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH)));
        }
        return R.ok(null);
    }

    @PostMapping("makeDir")
    public Object makeDir(@RequestBody Params params) {
        String folderName = StrUtil.subAfter(params.getRelativePath(), StrUtil.SLASH, true);
        return create(folderName, params.getRelativePath(), true, false);
    }

    @PostMapping("rename")
    public Object rename(@RequestBody Params params) {

        String relativePath = params.getRelativePath();
        List<TFile> files = fileMapper.selectList(new LambdaQueryWrapper<TFile>()
                .select(TFile::getRelativePath, TFile::getOriginalFileName, TFile::getId)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .and(wrapper -> wrapper
                        .eq(TFile::getRelativePath, relativePath)
                        .or()
                        .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH)));
        );
        String fileName = FileUtil.getName(params.getRelativePath());
        for (TFile file : files) {
            String suf = StrUtil.removePrefix(file.getRelativePath(), params.getRelativePath());
            if (StrUtil.isEmpty(suf)) {
                file.setOriginalFileName(params.getTargetName());
            }
            String newPre = StrUtil.removeSuffix(params.getRelativePath(), fileName) + params.getTargetName();
            file.setRelativePath(newPre + suf);
        }
        fileService.updateBatchById(files);
        return R.ok(null);
    }

    @GetMapping("chunkUploadFile")
    public Object chunkFile(Params params) {
        if (FileUtil.containsInvalid(params.getFilename())) {
            throw new ServiceException("文件名不合法");
        }
        TFieStorage target = fileStoreMapper.selectOne(new LambdaQueryWrapper<TFieStorage>()
                .select(TFieStorage::getId, TFieStorage::getStatus)
                .ne(TFieStorage::getStatus, FileStatusEnum.DELETED)
                .eq(TFieStorage::getIdentifier, params.getIdentifier())
        );

        if (target != null) {
            if (target.getStatus().equals(FileStatusEnum.CREATED)) {
                return new Response().setId(target.getId()).setSkipUpload(true);
            }
            List<Integer> uploaded = new ArrayList<>(map.getOrDefault(params.getIdentifier(), new HashSet<>()));
            return new Response().setId(target.getId()).setSkipUpload(false).setUploaded(uploaded);
        }

        String realFilePath = fileUtilService.absPath(null, params.getIdentifier());
        target = new TFieStorage()
                .setIdentifier(params.getIdentifier())
                .setPath(realFilePath)
                .setStatus(FileStatusEnum.NEW);
        fileStoreMapper.insert(target);
        return new Response().setId(target.getId()).setSkipUpload(false);
    }

    @Data
    @Accessors(chain = true)
    public static class Response {
        private Boolean skipUpload;
        private Long id;
        private List<Integer> uploaded;
    }

    @PostMapping("chunkUploadFile")
    public Object chunkUploadFile(@RequestBody MultipartFile file, Params params) throws IOException {

        String realFilePath = fileUtilService.absPath(null,
                params.getFilename() + DateUtil.format(new Date(), "yyyyMMdd_HHmmss"));

        Integer chunkNumber = params.getChunkNumber();
        Integer totalChunks = params.getTotalChunks();
        String identifier = params.getIdentifier();
        map.putIfAbsent(identifier, new HashSet<>());
        Set<Integer> set = map.get(identifier);
        if (!set.contains(chunkNumber)) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtil.touch(realFilePath), "rw");
            randomAccessFile.seek((chunkNumber - 1) * params.getChunkSize());
            randomAccessFile.write(file.getBytes());
            randomAccessFile.close();

            if (chunkNumber.equals(totalChunks) || chunkNumber == 1) {
                fileStoreMapper.update(null, new LambdaUpdateWrapper<TFieStorage>()
                        .set(TFieStorage::getStatus, chunkNumber.equals(totalChunks) ?
                                FileStatusEnum.CREATED : FileStatusEnum.CREATING)
                        .eq(TFieStorage::getId, params.getStorageId()));
            }
            set.add(chunkNumber);
        }

        return R.ok(null);
    }

    private TFile create(String fileName, String relativePath, Boolean isDir, Boolean touch) {
        if (StrUtil.isBlank(fileName)) {
            return new TFile().setId(TOP_FOLDER_ID);
        }

        // 判断是否有重名文件
        TFile sameNameFile = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                .eq(TFile::getRelativePath, relativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED));
        if (sameNameFile != null && touch) {
            return sameNameFile;
        }

        String newFileName = sameNameFile == null ? fileName : newFileName(fileName);
        String parentDirPath = StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
        String parentDirName = FileUtil.getName(parentDirPath);

        Long folderId = create(parentDirName, parentDirPath, true, true).getId();

        // 开始创建文件
        Date date = new Date();
        TFile file = new TFile()
                .setUpdateTime(date)
                .setStatus(FileStatusEnum.CREATED)
                .setType(isDir ? FileTypeEnum.DIR : FileTypeEnum.OTHER)
                .setFolderId(folderId)
                .setRelativePath(StrUtil.removeSuffix(relativePath, fileName) + newFileName)
                .setOriginalFileName(newFileName);
        fileMapper.insert(file);
        return file;
    }


    private String newFileName(String fileName) {
        String ext = FileUtil.extName(fileName);
        String mainName = FileUtil.mainName(fileName);
        if (StrUtil.isBlank(ext)) {
            return mainName + DateUtil.format(new Date(), "yyyyMMdd_HHmmss");
        } else {
            return mainName + DateUtil.format(new Date(), "yyyyMMdd_HHmmss") + StrUtil.DOT + ext;
        }
    }

    @PostMapping("merge")
    public Object merge(@RequestBody Params params) {

        String filename = params.getFilename();
        String relativePath = FileUtil.normalize(params.getRelativePath());

        // 判断是否有重名文件
        TFile sameNameFile = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                .select(TFile::getId)
                .eq(TFile::getRelativePath, relativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .last("limit 1"));

        if (sameNameFile != null) {
            filename = newFileName(filename);
        }

        String parentDirPath = StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
        String parentDirName = FileUtil.getName(parentDirPath);

        TFile parentDir = create(parentDirName, parentDirPath, true, true);

        TFile file = new TFile()
                .setStatus(FileStatusEnum.CREATED)
                .setType(FileTypeEnum.OTHER)
                .setStorageId(params.getStorageId())
                .setSize(params.getTotalSize())
                .setRelativePath(parentDirPath + StrUtil.SLASH + filename)
                .setOriginalFileName(filename)
                .setFolderId(parentDir == null ? TOP_FOLDER_ID : parentDir.getId());
        fileMapper.insert(file);
        return R.ok(file);
    }


    @PostMapping("copy")
    public Object copy(@RequestBody Params params) {
        return moveOrCopy(params.getFileList(), false);
    }

    @PostMapping("move")
    public Object move(@RequestBody Params params) {
        return moveOrCopy(params.getFileList(), true);
    }

    private List<TFile> moveOrCopy(List<FileVO> fileList, boolean move) {
        List<TFile> res = new ArrayList<>();
        Map<String, Long> targetFolderIdMap = new HashMap<>(2);
        for (FileVO fileVO : fileList) {
            String relativePath = fileVO.getRelativePath();
            String targetPath = fileVO.getTargetPath();

            if (targetPath.startsWith(relativePath)) {
                throw new ServiceException("不能复制/移动到自身目录或子目录");
            }

            if (!targetFolderIdMap.containsKey(targetPath)) {
                TFile targetDir = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                        .select(TFile::getId)
                        .eq(TFile::getRelativePath, targetPath)
                        .eq(TFile::getStatus, FileStatusEnum.CREATED));
                if (targetDir != null) {
                    targetFolderIdMap.putIfAbsent(targetPath, targetDir.getId());
                }
            }

            String fileName = FileUtil.getName(relativePath);
            List<TFile> files = fileMapper.selectList(new LambdaQueryWrapper<TFile>()
                    .select(TFile::getOriginalFileName, TFile::getSize, TFile::getStatus, TFile::getType,
                            TFile::getStorageId, TFile::getId)
                    .eq(TFile::getStatus, FileStatusEnum.CREATED)
                    .and(wrapper -> wrapper
                            .eq(TFile::getRelativePath, relativePath)
                            .or()
                            .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH)));


            for (TFile file : files) {
                if (!move) {
                    file.setId(null);
                }

                file.setFolderId(targetFolderIdMap.get(targetPath));
                file.setRelativePath(targetPath + StrUtil.SLASH + fileName);
            }
            fileService.updateBatchById(files);
        }
        return res;
    }


}
