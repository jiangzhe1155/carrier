package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.aspect.CommonLog;
import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.cn.jiangzhe.admin.entity.FileStatusEnum;
import org.cn.jiangzhe.admin.entity.FileTypeEnum;
import org.cn.jiangzhe.admin.entity.TFile;
import org.cn.jiangzhe.admin.service.FileServiceImpl;
import org.cn.jiangzhe.admin.service.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    Map<String, Boolean[]> map = new ConcurrentHashMap<>();

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    FileUtilService fileUtilService;

    @Autowired
    FileMapper fileMapper;

    public static String DEMO_DIR = "public/";
    public static String TMP_DIR = "tmp/";

    @PostMapping("uploadFile")
    public Object uploadFile(@RequestBody MultipartFile file, Params params) {
        if (file == null || FileUtil.containsInvalid(file.getOriginalFilename())) {
            return R.failed("文件名为空或包含非法字符 \\ / : * ? \" < > |");
        }
        return fileService.uploadFile(file, params.getRelativePath());
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Params {
        private String filename;
        private String relativePath;
        private Integer chunkNumber;
        private Integer chunkSize;
        private Integer currentChunkSize;
        private Integer totalSize;
        private String identifier;
        private Integer totalChunks;
        private List<String> relativePaths;
        private String originName;
        private String targetName;
        private String md5;
        private String targetPath;
        private Boolean isDir;
        @JsonIgnore
        private MultipartFile file;
    }

    @PostMapping("listFile")
    public Object listFile(@RequestBody Params params) {
        FileUtil.mkdir(DEMO_DIR);
        return fileService.listFile(params.getRelativePath());
    }

    @PostMapping("deleteFile")
    public Object deleteFile(@RequestBody Params params) {
        return fileService.deleteFile(params.getRelativePath(), params.getFilename());
    }

    @PostMapping("makeDir")
    public Object makeDir(@RequestBody Params params) {
        String fileName = fileUtilService.formatFileName(params.getFilename());
        if (FileUtil.containsInvalid(fileName)) {
            return R.failed("文件名为空或包含非法字符 \\ / : * ? \" < > |");
        }
        return fileService.makeDir(params.getRelativePath(), fileName);
    }

    @PostMapping("rename")
    public Object rename(@RequestBody Params params) {
        if (params.getOriginName().equals(params.getTargetName())) {
            return R.failed("文件(夹)重名");
        }
        return fileService.rename(params.getRelativePath(), params.getOriginName(), params.getTargetName());
    }

    /**
     * 1.根据上传的文件生成一个唯一id
     * 2.服务端生成这个分片的md5
     * 3.判断临时文件表（或者缓存）中是否存在这分片的MD5，存在直接返回
     * 3.不存在的情况下直接拷贝片到指定路径文件，成功后添加分片缓存
     *
     * @param params
     * @return
     * @throws IOException
     */
    @PostMapping("chunkUploadFile")
    public Object chunkUploadFile(Params params) throws IOException {

        String realFilePath = fileUtilService.absPath(null, params.getIdentifier());
        String fileName = params.getFilename();
        if (params.getChunkNumber() == 1) {
            TFile file = create(fileName, params.getRelativePath(), false, false);
        }
        File chunkFile = FileUtil.touch(realFilePath);

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(chunkFile, "rw")) {
            randomAccessFile.seek((params.getChunkNumber() - 1) * params.getChunkSize());
            randomAccessFile.write(params.getFile().getBytes());
            map.putIfAbsent(params.getIdentifier(), new Boolean[params.getTotalChunks()]);
            Boolean[] bitMap = map.get(params.getIdentifier());
            bitMap[params.getChunkNumber() - 1] = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return R.ok(null);
    }

    private TFile create(String fileName, String relativePath, Boolean isDir, Boolean touch) {

        if (StrUtil.isBlank(fileName)) {
            return new TFile().setId(0L);
        }

        // 判断是否有重名文件
        TFile sameNameFile = fileMapper.selectOne(new LambdaQueryWrapper<TFile>()
                .eq(TFile::getRelativePath, relativePath)
                .ne(TFile::getStatus, FileStatusEnum.DELETED));
        if (sameNameFile != null && touch) {
            return sameNameFile;
        }

        fileName = sameNameFile == null ? fileName : newFileName(fileName);
        String parentDirPath = StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
        String parentDirName = FileUtil.getName(parentDirPath);

        Long folderId = create(parentDirPath, parentDirName, true, true).getId();

        // 开始创建文件
        Date date = new Date();
        TFile file = new TFile()
                .setCreateTime(date)
                .setUpdateTime(date)
                .setStatus(isDir ? FileStatusEnum.CREATED : FileStatusEnum.NEW)
                .setType(isDir ? FileTypeEnum.DIR : FileTypeEnum.OTHER)
                .setFolderId(folderId)
                .setRelativePath(relativePath)
                .setOriginalFileName(fileName);
        fileMapper.insert(file);
        return file;
    }


    private String newFileName(String fileName) {
        String ext = FileUtil.extName(fileName);
        String mainName = FileUtil.mainName(fileName);
        return mainName + DateUtil.format(new Date(), "yyyyMMdd_HHmmss") + StrUtil.DOT + ext;
    }


    @PostMapping("create")
    public Object create(@RequestBody Params params) {
        return create(params.getFilename(), params.getRelativePath(), params.getIsDir(), true);
    }

    @PostMapping("merge")
    public Object merge(@RequestBody Params params) {
        return create(params.getFilename(), params.getRelativePath(), params.getIsDir(), true);
    }


}
