package org.jz.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jz.admin.entity.TFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author jz
 * @date 2020/06/11
 */
public interface FileService extends IService<TFile> {

    Boolean uploadFile(MultipartFile multipartFile, String relativePath) throws IOException;

}
