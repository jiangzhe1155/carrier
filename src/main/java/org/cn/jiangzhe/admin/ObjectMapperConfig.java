package org.cn.jiangzhe.admin;

import cn.hutool.core.io.FileUtil;
import org.cn.jiangzhe.admin.controller.FileController;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * @author 江哲
 * @date 2020/06/14
 */
@Configuration
public class ObjectMapperConfig {

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(FileUtil.getAbsolutePath(FileController.TMP_DIR));
        return factory.createMultipartConfig();
    }
}
