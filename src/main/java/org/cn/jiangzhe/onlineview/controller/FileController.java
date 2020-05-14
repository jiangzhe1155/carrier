package org.cn.jiangzhe.onlineview.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.onlineview.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RestController
public class FileController {

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("upload")
    public R<Object> upload(@RequestBody MultipartFile multipartFile) {
        String filename = multipartFile.getOriginalFilename();
        log.info("OriginalFilename:{}  Name:{} ", multipartFile.getOriginalFilename(), multipartFile.getName());
        for (int i = filename.length() - 1; i >= 0; i--) {
            if (CharUtil.isFileSeparator(filename.charAt(i))) {
                filename = filename.substring(i + 1);
                break;
            }
        }

        try (InputStream in = multipartFile.getInputStream()) {
            String filePath = StrUtil.join(File.separator, FileUtil.getUserHomePath(), "file", filename);
            log.info(filePath);
            FileUtil.writeFromStream(in, filePath);

        } catch (IOException e) {
            throw new ServiceException("抱歉服务内部异常");
        }
        return R.ok(null);
    }

}
