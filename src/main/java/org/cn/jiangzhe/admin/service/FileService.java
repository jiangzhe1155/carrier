package org.cn.jiangzhe.admin.service;

import org.cn.jiangzhe.admin.CommonFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author jz
 * @date 2020/06/11
 */
public interface FileService {

    List<CommonFile> uploadFile(MultipartFile multipartFile, String relativePath) throws IOException;

    List<CommonFile> uploadFiles(String relativePath, MultipartFile... multipartFile) throws IOException;

    Object listFiles(String relativePath);
}
