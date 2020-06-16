package org.cn.jiangzhe.admin.service;

import ch.qos.logback.core.util.FileSize;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.aspect.ServiceException;
import org.cn.jiangzhe.admin.dto.CommonFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jz
 * @date 2020/06/11
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileUtilService fileUtilService;

    @Override
    public Boolean uploadFile(MultipartFile multipartFile, String relativePath) {
        String absPath = fileUtilService.absPath(relativePath, multipartFile.getOriginalFilename());
        log.info("上传路径：{}", absPath);
        File file = FileUtil.file(absPath);
        if (FileUtil.exist(file)) {
            throw new ServiceException("存在同名文件(夹)");
        }

        try (InputStream in = multipartFile.getInputStream()) {
            FileUtil.writeFromStream(in, file);
        } catch (IOException e) {
            throw new ServiceException("抱歉上传文件失败");
        }
        return true;
    }

    public Boolean deleteFile(String relativePath, String fileName) {
        String absPath = fileUtilService.absPath(relativePath, fileName);
        File file = FileUtil.file(absPath);
        if (!FileUtil.del(file)) {
            throw new ServiceException("抱歉文件不存在");
        }
        return true;
    }

    public List<CommonFile> listFile(String relativePath) {
        String dir = fileUtilService.absPath(relativePath, null);
        if (!FileUtil.exist(dir)) {
            throw new ServiceException("抱歉文件不存在");
        }
        File[] files = FileUtil.ls(dir);
        List<CommonFile> commonFiles = Arrays.stream(files).map(file -> CommonFile.builder()
                .fileName(file.getName())
                .isDir(file.isDirectory())
                .lastModifyTime(FileUtil.lastModifiedTime(file))
                .size(new FileSize(FileUtil.size(file)).toString())
                .fileType(file.isDirectory() ? null : fileUtilService.fileType(file.getName()))
                .build()).collect(Collectors.toList());
        return commonFiles;
    }

    public Boolean createDir(String relativePath, String fileName) {
        String absPath = fileUtilService.absPath(relativePath, fileName);
        File file = FileUtil.file(absPath);
        if (FileUtil.exist(file)) {
            throw new ServiceException("存在同名文件(夹)");
        }
        File mkdir = FileUtil.mkdir(absPath);
        return true;
    }


}
