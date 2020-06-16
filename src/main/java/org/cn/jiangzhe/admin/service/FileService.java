package org.cn.jiangzhe.admin.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author jz
 * @date 2020/06/11
 */
public interface FileService {

    Boolean uploadFile(MultipartFile multipartFile, String relativePath) throws IOException;

}
