package org.cn.jiangzhe.onlineview.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.onlineview.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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

    private String demoDir = "file";

    @PostMapping("upload")
    public R<Object> upload(@RequestBody MultipartFile multipartFile) {
        String filename = FileUtil.getName(multipartFile.getOriginalFilename());
        log.info("OriginalFilename:{}  Name:{} ", multipartFile.getOriginalFilename(), filename);
        try (InputStream in = multipartFile.getInputStream()) {
            String filePath = StrUtil.join(File.separator, FileUtil.getUserHomePath(), demoDir, filename);
            FileUtil.writeFromStream(in, filePath);
            return R.ok(filePath);
        } catch (IOException e) {
            throw new ServiceException("抱歉服务内部异常");
        }
    }

    @PostMapping("listFiles")
    public R getFiles() {
        List list = new ArrayList<Map<String, String>>();
        List<String> strings = FileUtil.listFileNames(
                StrUtil.join(File.separator, FileUtil.getUserHomePath(), demoDir));
        return R.ok(strings);
    }

    @PostMapping("previewFile")
    public R previewFile(HttpServletRequest req, @RequestBody @Nullable String url) {

        return R.ok(null);
    }


}
