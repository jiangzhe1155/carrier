package org.cn.jiangzhe.admin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
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
            throw new ServiceException(StrUtil.format("存在同名文件 {}", file.getName()));
        }
        try (InputStream in = multipartFile.getInputStream()) {
            FileUtil.writeFromStream(in, file);
        } catch (IOException e) {
            throw new ServiceException(StrUtil.format("抱歉上传文件 {} 失败", file.getName()));
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
        List<CommonFile> commonFiles =
                Arrays.stream(files).filter(file -> !file.isHidden()).map(file -> CommonFile.builder()
                .fileName(file.getName())
                .isDir(file.isDirectory())
                .lastModifyTime(FileUtil.lastModifiedTime(file))
                .size(FileUtil.readableFileSize(file))
                .fileType(file.isDirectory() ? null : FileUtil.extName(file.getName()))
                .build()).collect(Collectors.toList());
        return commonFiles;
    }

    public Boolean makeDir(String relativePath, String fileName) {
        String absPath = fileUtilService.absPath(relativePath, fileName);
        File file = FileUtil.file(absPath);
        if (FileUtil.exist(file)) {
            throw new ServiceException("存在同名文件(夹)");
        }
        File mkdir = FileUtil.mkdir(absPath);
        return true;
    }


    public Boolean rename(String relativePath, String originName, String targetName) {
        File originFile = FileUtil.file(fileUtilService.absPath(relativePath, originName));
        File targetFile = FileUtil.file(fileUtilService.absPath(relativePath, targetName));
        if (!FileUtil.exist(originFile)) {
            throw new ServiceException("抱歉文件不存在");
        }
        if (FileUtil.exist(targetFile)) {
            throw new ServiceException("存在重名文件(夹)");
        }
        FileUtil.rename(originFile, targetName, false, false);
        return true;
    }
}
