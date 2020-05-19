package org.cn.jiangzhe.onlineview.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

import static org.cn.jiangzhe.onlineview.controller.FileController.DEMO_DIR;

/**
 * @author jz
 * @date 2020/05/19
 */
@RestController
public class FilePreViewController {

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("previewFile")
    public void previewFile(HttpServletResponse response, String url) {
        String path = StrUtil.join(File.separator, DEMO_DIR, url);
    }

}
