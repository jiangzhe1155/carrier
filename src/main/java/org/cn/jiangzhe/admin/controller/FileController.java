package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.CommonFile;
import org.cn.jiangzhe.admin.aspect.CommonLog;
import org.cn.jiangzhe.admin.dao.TFileEngineMapper;
import org.cn.jiangzhe.admin.service.FileService;
import org.cn.jiangzhe.admin.service.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@CommonLog
@RestController
public class FileController {

    @Autowired
    FileService fileService;

    public static String DEMO_DIR = "public/";
    public static String TMP_DIR = "tmp";

    @Autowired
    TFileEngineMapper engineMapper;


    @PostMapping("uploadFile")
    public R uploadFile(@RequestBody  MultipartFile multipartFile,
                        Params params) throws IOException {
        fileService.uploadFile(multipartFile, params.getRelativePath());
        return R.ok(null);
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Params {
        private String relativePath;
        private Integer channel;
        private String fileName;
    }

    @PostMapping("listFile")
    public R getFiles(@RequestBody Params params) {
        File[] files = FileUtil.ls(FileController.DEMO_DIR + params.getRelativePath());
        List<CommonFile> collect = Arrays.stream(files).map(file -> CommonFile.builder()
                .fileName(file.getName())
                .isDir(file.isDirectory())
                .size(FileUtil.size(file))
                .fileType(file.isDirectory() ? null : FileServiceImpl.getFileType(file.getName()))
                .build()).collect(Collectors.toList());
        return R.ok(collect);
    }

    @PostMapping("deleteFile")
    public R uploadFile(@RequestBody Params params) throws IOException {
        String absPath = FileServiceImpl.getAbsPath(params.getRelativePath(), params.fileName);
        boolean del = FileUtil.del(absPath);
        return R.ok(del);
    }

}
