package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.aspect.CommonLog;
import org.cn.jiangzhe.admin.dao.FileMapper;
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
        FileUtil.mkdir(DEMO_DIR);
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

    /**
     * 1.根据上传的文件生成一个唯一id
     * 2.服务端生成这个分片的md5
     * 3.判断临时文件表（或者缓存）中是否存在这分片的MD5，存在直接返回
     * 3.不存在的情况下直接拷贝片到指定路径文件，成功后添加分片缓存
     *
     * @param file
     * @param params
     * @return
     * @throws IOException
     */
    @PostMapping("chunkUploadFile")
    public Object chunkUploadFile(@RequestBody MultipartFile file, Params params) throws IOException {

        File chunkFile = FileUtil.touch(fileUtilService.absPath(params.getRelativePath(), params.getFileName()));
        log.info("上传路径：{}\t文件名:{}", chunkFile.getAbsolutePath(), file.getName());
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(chunkFile, "rw");) {
            randomAccessFile.seek(params.getChunk() * params.getEachSize());
            randomAccessFile.write(file.getBytes());
            map.putIfAbsent(params.getId(), new Boolean[params.getChunks()]);
            Boolean[] bitMap = map.get(params.getId());
            bitMap[params.getChunk()] = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return R.ok(null);
    }

    @Autowired
    FileMapper fileMapper;

    @PostMapping("mergeUploadFile")
    public Object mergeUploadFile(@RequestBody Params params) {
        Boolean[] checkChunks = map.get(params.id);
        for (Boolean c : checkChunks) {
            if (!c) {
                return R.failed("校验失败");
            }
        }

        DateTime now = DateUtil.parseTime(params.id);
        TFile file = new TFile();
        file.setCreateTime(now);
        file.setUpdateTime(now);
        return R.ok(null);
    }

}
