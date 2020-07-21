package org.jz.admin.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jz.admin.aspect.CommonLog;
import org.jz.admin.aspect.ServiceException;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFileStore;
import org.jz.admin.entity.TFile;
import org.jz.admin.mapper.FileStoreMapper;
import org.jz.admin.mapper.FileMapper;
import org.jz.admin.service.FileServiceImpl;
import org.jz.admin.service.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static final Map<FileTypeEnum, List<String>> FILE_TYPE_ENUM_LIST_MAP =
            MapUtil.<FileTypeEnum, List<String>>builder()
                    .put(FileTypeEnum.IMAGE, Arrays.asList("gif", "jpg", "jpeg", "png", "bmp", "webp"))
                    .put(FileTypeEnum.DOCUMENT, Arrays.asList("doc", "txt", "docx", "pages", "epub", "pdf", "numbers",
                            "csv", "xls", "xlsx", "keynote", "ppt", "pptx"))
                    .put(FileTypeEnum.VIDEO, Arrays.asList("mp4", "m3u8", "rmvb", "avi", "swf", "3gp", "mkv", "flv",
                            "mp3", "wav", "wma", "ogg", "aac", "flac"))
                    .build();

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    FileUtilService fileUtilService;

    @Autowired
    FileMapper fileMapper;

    @Autowired
    FileStoreMapper fileStoreMapper;

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
        private List<Long> fidList;
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
                .select(TFile::getId, TFile::getFileName, TFile::getUpdateTime, TFile::getType,
                        TFile::getSize, TFile::getRelativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(params.getType() != null, TFile::getType, params.getType())
                .eq(TFile::getFolderId, folderId)
                .orderByAsc(TFile::getType)
                .orderByDesc(TFile::getUpdateTime));
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
        return createDir(folderName, params.getRelativePath(), false);
    }

    @PostMapping("rename")
    public Object rename(@RequestBody Params params) {

        String relativePath = params.getRelativePath();
        String fileName = FileUtil.getName(relativePath);
        String targetName = params.getTargetName();
        String targetPath = StrUtil.removeSuffix(relativePath, fileName) + targetName;

        TFile sameNameFile = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                .select(TFile::getId)
                .eq(TFile::getRelativePath, targetPath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED));
        if (sameNameFile != null) {
            targetName = newFileName(targetName);
        }

        List<TFile> files = fileMapper.selectList(new LambdaQueryWrapper<TFile>()
                .select(TFile::getRelativePath, TFile::getFileName, TFile::getId)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .and(wrapper -> wrapper
                        .eq(TFile::getRelativePath, relativePath)
                        .or()
                        .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH)));

        String filePrePath = StrUtil.removeSuffix(relativePath, fileName) + targetName;
        for (TFile file : files) {
            String suf = StrUtil.removePrefix(file.getRelativePath(), relativePath);
            if (StrUtil.isEmpty(suf)) {
                //说明就是要改名的文件
                file.setFileName(targetName);
                file.setType(parseType(FileUtil.extName(targetName)));
            }
            file.setRelativePath(filePrePath + suf);
        }
        fileService.updateBatchById(files);
        return R.ok(null);
    }

    @GetMapping("chunkUploadFile")
    public Object chunkFile(Params params) {

        if (FileUtil.containsInvalid(params.getFilename())) {
            throw new ServiceException("文件名不合法");
        }
        if (params.getTotalSize() <= 0) {
            throw new ServiceException("文件大小不能为0");
        }

        TFileStore target = fileStoreMapper.selectOne(new LambdaQueryWrapper<TFileStore>()
                .select(TFileStore::getId, TFileStore::getStatus)
                .ne(TFileStore::getStatus, FileStatusEnum.DELETED)
                .eq(TFileStore::getIdentifier, params.getIdentifier())
        );

        if (target != null) {
            if (target.getStatus().equals(FileStatusEnum.CREATED)) {
                return new Response().setId(target.getId()).setSkipUpload(true);
            }
            List<Integer> uploaded = new ArrayList<>(map.getOrDefault(params.getIdentifier(), new HashSet<>()));
            return new Response().setId(target.getId()).setSkipUpload(false).setUploaded(uploaded);
        }

        String realFilePath = fileUtilService.absPath(null,
                newFileName(params.getFilename()));
        target = new TFileStore()
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
        String realFilePath = fileUtilService.absPath(null, newFileName(params.getFilename()));
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
                fileStoreMapper.update(null, new LambdaUpdateWrapper<TFileStore>()
                        .set(TFileStore::getStatus, chunkNumber.equals(totalChunks) ?
                                FileStatusEnum.CREATED : FileStatusEnum.CREATING)
                        .eq(TFileStore::getIdentifier, identifier));
            }
            set.add(chunkNumber);
        }
        return R.ok(null);
    }

    private TFile createDir(String fileName, String relativePath, Boolean touch) {
        if (StrUtil.isBlank(fileName)) {
            return new TFile().setId(TOP_FOLDER_ID);
        }

        // 判断是否有重名文件
        TFile sameNameFile = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                .select(TFile::getId)
                .eq(TFile::getRelativePath, relativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED));
        if (sameNameFile != null && touch) {
            return sameNameFile;
        }

        String newFileName = sameNameFile == null ? fileName : newFileName(fileName);
        String parentDirPath = StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
        String parentDirName = FileUtil.getName(parentDirPath);

        Long folderId = createDir(parentDirName, parentDirPath, true).getId();

        // 开始创建文件
        TFile file = new TFile()
                .setStatus(FileStatusEnum.CREATED)
                .setType(FileTypeEnum.DIR)
                .setFolderId(folderId)
                .setRelativePath(StrUtil.removeSuffix(relativePath, fileName) + newFileName)
                .setFileName(newFileName);
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
                .eq(TFile::getStatus, FileStatusEnum.CREATED));

        if (sameNameFile != null) {
            filename = newFileName(filename);
        }

        String parentDirPath = StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
        String parentDirName = FileUtil.getName(parentDirPath);
        TFile parentDir = createDir(parentDirName, parentDirPath, true);


        TFile file = new TFile()
                .setStatus(FileStatusEnum.CREATED)
                .setType(parseType(FileUtil.extName(filename)))
                .setStorageId(params.getStorageId())
                .setSize(params.getTotalSize())
                .setRelativePath(parentDirPath + StrUtil.SLASH + filename)
                .setFileName(filename)
                .setFolderId(parentDir == null ? TOP_FOLDER_ID : parentDir.getId());
        fileMapper.insert(file);
        return R.ok(file);
    }


    private FileTypeEnum parseType(String extName) {
        for (Map.Entry<FileTypeEnum, List<String>> entry : FILE_TYPE_ENUM_LIST_MAP.entrySet()) {
            FileTypeEnum fileTypeEnum = entry.getKey();
            List<String> types = entry.getValue();
            if (types.contains(extName)) {
                return fileTypeEnum;
            }
        }
        return FileTypeEnum.OTHER;
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

            String sufPath = StrUtil.subBefore(relativePath, StrUtil.SLASH, true);
            if (sufPath.equals(targetPath) || targetPath.startsWith(relativePath + StrUtil.SLASH)) {
                throw new ServiceException("不能复制/移动到自身目录或子目录");
            }

            if (StrUtil.isNotEmpty(targetPath)) {
                TFile targetDir = fileMapper.selectOne(new LambdaQueryWrapper<TFile>().select(TFile::getId)
                        .eq(TFile::getRelativePath, targetPath)
                        .eq(TFile::getStatus, FileStatusEnum.CREATED));
                if (targetDir != null) {
                    targetFolderIdMap.put(targetPath, targetDir.getId());
                }
            }

            String fileName = FileUtil.getName(relativePath);
            List<TFile> files = fileMapper.selectList(new LambdaQueryWrapper<TFile>()
                    .select(TFile::getFileName, TFile::getSize, TFile::getStatus, TFile::getType,
                            TFile::getStorageId, TFile::getId, TFile::getRelativePath, TFile::getFolderId)
                    .eq(TFile::getStatus, FileStatusEnum.CREATED)
                    .and(wrapper -> wrapper
                            .eq(TFile::getRelativePath, relativePath)
                            .or()
                            .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH)));


            // 判断是否有重名文件
            TFile sameNameFile = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                    .select(TFile::getId)
                    .eq(TFile::getRelativePath, targetPath + StrUtil.SLASH + fileName)
                    .eq(TFile::getStatus, FileStatusEnum.CREATED));

            if (sameNameFile != null) {
                fileName = newFileName(fileName);
            }

            String targetRelativePath = targetPath + StrUtil.SLASH + fileName;
            for (TFile file : files) {
                if (!move) {
                    file.setId(null);
                }

                if (file.getRelativePath().equals(relativePath)) {
                    //说明是根文件
                    file.setFolderId(targetFolderIdMap.get(targetPath));
                    file.setFileName(fileName);
                }

                file.setRelativePath(targetRelativePath + StrUtil.removePrefix(file.getRelativePath(), relativePath));
            }

            fileService.saveOrUpdateBatch(files);
        }
        return res;
    }


    @PostMapping("download")
    public void download(@RequestBody Params params, HttpServletResponse response) throws IOException {
        List<Long> fidList = params.getFidList();
        List<TFile> files = fileMapper.selectBatchIds(fidList);
        String commonPrefix = getCommonPrefix(files);
        String fileName;
        if (files.size() == 1 && !files.get(0).getType().equals(FileTypeEnum.DIR)) {
            fileName = files.get(0).getFileName();
        } else {
            fileName = StrUtil.subBefore(files.get(0).getFileName(), StrUtil.DOT, true) + ".zip";
        }
        LambdaQueryWrapper<TFile> wrapper = new LambdaQueryWrapper<>();
        for (TFile file : files) {
            wrapper.or(w -> w.eq(TFile::getRelativePath, file.getRelativePath())
                    .or()
                    .likeRight(file.getType().equals(FileTypeEnum.DIR), TFile::getRelativePath,
                            file.getRelativePath() + StrUtil.SLASH));
        }
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, URLUtil.encode(fileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        List<TFile> fileListWithRealPath = fileMapper.getFileListWithRealPath(wrapper);
        OutputStream outputStream;
        if (files.size() == 1 && !files.get(0).getType().equals(FileTypeEnum.DIR)) {
            TFile file = fileListWithRealPath.get(0);
            outputStream = response.getOutputStream();
            BufferedInputStream inputStream = FileUtil.getInputStream(file.getPath());
            IoUtil.copy(inputStream, outputStream);
            inputStream.close();
        } else {
            outputStream = new ZipOutputStream(response.getOutputStream());
            zip((ZipOutputStream) outputStream, fileListWithRealPath, commonPrefix);
        }
        outputStream.close();
    }

    private void zip(ZipOutputStream outputStream, List<TFile> fileListWithRealPath, String commonPrefix) throws IOException {
        for (TFile file : fileListWithRealPath) {
            if (!file.getType().equals(FileTypeEnum.DIR)) {
                outputStream.putNextEntry(new ZipEntry(StrUtil.removePrefix(file.getRelativePath(), commonPrefix)));
                BufferedInputStream inputStream = FileUtil.getInputStream(file.getPath());
                IoUtil.copy(inputStream, outputStream);
                inputStream.close();
            } else {
                outputStream.putNextEntry(new ZipEntry(StrUtil.removePrefix(file.getRelativePath() + StrUtil.SLASH,
                        commonPrefix)));
            }
            outputStream.closeEntry();
        }
    }

    private String getCommonPrefix(List<TFile> files) {

        if (CollUtil.isEmpty(files)) {
            return StrUtil.EMPTY;
        }

        int l = 0;
        int r = files.get(0).getRelativePath().indexOf(StrUtil.SLASH, l);
        while (r != -1) {
            String tmp = files.get(0).getRelativePath().substring(l, r);
            boolean can = true;
            for (TFile file : files) {
                int idx = file.getRelativePath().indexOf(StrUtil.SLASH, l);
                if (idx != r || !tmp.equals(file.getRelativePath().substring(l, idx))) {
                    can = false;
                    break;
                }
            }
            if (can) {
                l = r + 1;
                r = files.get(0).getRelativePath().indexOf(StrUtil.SLASH, l);
            }
        }

        return files.get(0).getRelativePath().substring(0, l);
    }


}
