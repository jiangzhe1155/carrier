package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.CommonFile;
import org.cn.jiangzhe.admin.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RestController
public class FileController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    FileService fileService;

    public static String DEMO_DIR = StrUtil.join(File.separator, FileUtil.getUserHomePath(), "file");

    @PostMapping("uploadFile")
    public R uploadFile(@RequestBody MultipartFile multipartFile, String relativePath) throws IOException {
        return uploadFiles(new MultipartFile[]{multipartFile}, relativePath);
    }

    @PostMapping("uploadFiles")
    public R uploadFiles(@RequestBody MultipartFile[] multipartFiles, String relativePath) throws IOException {
        for (MultipartFile multipartFile : multipartFiles) {
            if (multipartFile == null || StrUtil.isEmpty(multipartFile.getOriginalFilename())) {
                return R.failed("抱歉文件为空");
            }
        }
        List<CommonFile> commonFiles = fileService.uploadFiles(relativePath, multipartFiles);
        return R.ok(commonFiles);
    }


    @PostMapping("listFiles")
    public R getFiles(String relativePath) {
        fileService.listFiles(relativePath);
        return R.ok(null);
    }

}
