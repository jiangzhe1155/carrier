package org.cn.jiangzhe.onlineview.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.onlineview.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RestController
public class FileController {

    @Autowired
    ObjectMapper objectMapper;

    public static String DEMO_DIR = StrUtil.join(File.separator, FileUtil.getUserHomePath(), "file");


    @PostMapping("upload")
    public R<Object> upload(@RequestBody MultipartFile multipartFile) {
        String filename = FileUtil.getName(multipartFile.getOriginalFilename());
        log.info("OriginalFilename:{}  Name:{} ", multipartFile.getOriginalFilename(), filename);
        try (InputStream in = multipartFile.getInputStream()) {
            String filePath = StrUtil.join(File.separator, DEMO_DIR, filename);
            FileUtil.writeFromStream(in, filePath);
            return R.ok(filePath);
        } catch (IOException e) {
            throw new ServiceException("抱歉服务内部异常");
        }
    }

    @PostMapping("listFiles")
    public R getFiles() {
        List list = new ArrayList<Map<String, String>>();
        List<String> strings = FileUtil.listFileNames(DEMO_DIR);
        return R.ok(strings);
    }

    @GetMapping("previewFile")
    public R previewFile(HttpServletResponse response, String url){
        String path = StrUtil.join(File.separator, DEMO_DIR, url);
        if (!FileUtil.exist(path)) {
            return R.failed("抱歉找不到文件");
        }
        return R.ok(null);
    }


}
