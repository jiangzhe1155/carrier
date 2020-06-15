package org.cn.jiangzhe.admin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.CommonFile;
import org.cn.jiangzhe.admin.ServiceException;
import org.cn.jiangzhe.admin.controller.FileController;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jz
 * @date 2020/06/11
 */
@Slf4j
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

        for (CommonFile file : files) {
            log.info("上传路径：{}", FileUtil.normalize(FileController.DEMO_DIR + file.getFileName()));
            try (InputStream in = file.getIn()) {
                FileUtil.writeFromStream(in, FileController.DEMO_DIR + file.getFileName());
            } catch (IOException e) {
                throw new ServiceException("抱歉服务内部异常");
            }
        }

        return files;
    }

    @Override
    public Object listFiles(String relativePath) {
        return FileUtil.listFileNames(relativePath);
    }

}
