package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.aspect.CommonLog;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@CommonLog
@RestController
public class FileController {

    private Map<String, PriorityBlockingQueue<Params>> map = new HashMap<>();

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    FileUtilService fileUtilService;

    public static String DEMO_DIR = "public/";
    public static String TMP_DIR = "tmp/";

    @PostMapping("uploadFile")
    public Object uploadFile(@RequestBody MultipartFile multipartFile, Params params) {
        if (multipartFile == null || FileUtil.containsInvalid(multipartFile.getOriginalFilename())) {
            return R.failed("文件名为空或包含非法字符 \\ / : * ? \" < > |");
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
        private String originName;
        private String targetName;
        private String id;
        private Integer chunks;
        private Integer chunk;
        private Integer eachSize;
        private Integer fullSize;
        private String md5;
    }

    @PostMapping("listFile")
    public Object listFile(@RequestBody Params params) {
        return fileService.listFile(params.getRelativePath());
    }

    @PostMapping("deleteFile")
    public Object deleteFile(@RequestBody Params params) {
        return fileService.deleteFile(params.getRelativePath(), params.getFileName());
    }

    @PostMapping("makeDir")
    public Object makeDir(@RequestBody Params params) {
        String fileName = fileUtilService.formatFileName(params.getFileName());
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


    @PostMapping("chunkUploadFile")
    public Object chunkUploadFile(@RequestBody MultipartFile multipartFile, Params params) throws IOException {
        if (params.chunk == 2 || params.chunk == 3) {
            log.info("文件大小 ：{}", multipartFile.getSize());
            return R.failed("抱歉出错了");
        }
        PriorityBlockingQueue<Params> priorityBlockingQueue = map.getOrDefault(params.id,
                new PriorityBlockingQueue<>(params.chunks, Comparator.comparing(Params::getChunk)));
        priorityBlockingQueue.add(params);
        File file = FileUtil.file(fileUtilService.absPath(params.getRelativePath(), params.getFileName()));
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");) {
            randomAccessFile.seek(params.getChunk() * params.getEachSize());
            randomAccessFile.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return R.ok(null);
    }

    @PostMapping("mergeUploadFile")
    public Object mergeUploadFile(Params params) {
        PriorityBlockingQueue<Params> priorityBlockingQueue = map.getOrDefault(params.id,
                new PriorityBlockingQueue<>(params.chunks, Comparator.comparing(Params::getChunk)));
        System.out.println("校验  " + priorityBlockingQueue);
        if (priorityBlockingQueue.size() != params.chunks) {
            return R.failed("校验失败");
        }
        return R.ok(null);
    }


}
