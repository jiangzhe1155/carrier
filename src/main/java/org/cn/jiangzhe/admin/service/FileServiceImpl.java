package org.cn.jiangzhe.admin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.cn.jiangzhe.admin.CommonFile;
import org.cn.jiangzhe.admin.ServiceException;
import org.cn.jiangzhe.admin.controller.FileController;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jz
 * @date 2020/06/11
 */
@Service
public class FileServiceImpl implements FileService {

    @Override
    public List<CommonFile> uploadFile(MultipartFile file, String relativePath) throws IOException {
        return uploadFiles(relativePath, file);
    }

    @Override
    public List<CommonFile> uploadFiles(String relativePath, MultipartFile... multipartFiles) throws IOException {
        List<CommonFile> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            files.add(CommonFile.builder()
                    .fileName(fileName)
                    .fileType(StrUtil.subAfter(fileName, '.', true))
                    .in(multipartFile.getInputStream())
                    .build());
        }

        String basePath = FileUtil.normalize(
                StrUtil.join(File.separator, FileController.DEMO_DIR, StrUtil.nullToEmpty(relativePath)));

        for (CommonFile file : files) {
            try (InputStream in = file.getIn()) {
                FileUtil.writeFromStream(in, StrUtil.join(File.separator, basePath, file.getFileName()));
            } catch (IOException e) {
                throw new ServiceException("抱歉服务内部异常");
            }
        }

        return files;
    }

    @Override
    public void listFiles(String relativePath) {

    }
}
