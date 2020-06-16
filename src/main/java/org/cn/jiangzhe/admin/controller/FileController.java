package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.aspect.CommonLog;
import org.cn.jiangzhe.admin.aspect.ServiceException;
import org.cn.jiangzhe.admin.service.FileServiceImpl;
import org.cn.jiangzhe.admin.service.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@CommonLog
@RestController
public class FileController {

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    FileUtilService fileUtilService;

    public static String DEMO_DIR = "public/";
    public static String TMP_DIR = "tmp";

    @PostMapping("uploadFile")
    public Object uploadFile(@RequestBody MultipartFile multipartFile, Params params) {
        if (multipartFile == null || StrUtil.isBlank(multipartFile.getOriginalFilename())) {
            throw new ServiceException("文件为空");
        }
        return fileService.uploadFile(multipartFile, params.getRelativePath());
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Params {
        private String relativePath;
        private Integer channel;
        private String fileName;
        private List<String> relativePaths;
    }

    @PostMapping("listFile")
    public Object listFile(@RequestBody Params params) {
        return fileService.listFile(params.getRelativePath());
    }

    @PostMapping("deleteFile")
    public Object deleteFile(@RequestBody Params params) {
        return fileService.deleteFile(params.getRelativePath(), params.getFileName());
    }

    @PostMapping("createDir")
    public Object createDir(@RequestBody Params params) {
        if (StrUtil.isBlank(params.getFileName())) {
            throw new ServiceException("文件(夹)名字不能为空");
        }
        return fileService.createDir(params.getRelativePath(), params.getFileName());
    }


}
