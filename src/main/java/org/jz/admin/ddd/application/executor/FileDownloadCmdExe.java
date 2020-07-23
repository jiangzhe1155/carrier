package org.jz.admin.ddd.application.executor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.FileConvertor;
import org.jz.admin.ddd.application.dto.FileDownloadCmd;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileDownloadCmdExe {
    @Autowired
    FileRepositoryImpl fileRepository;

    @Autowired
    HttpServletResponse response;

    public Response execute(FileDownloadCmd cmd) {
        List<File> files =
                fileRepository.listByIds(cmd.getFidList()).stream().map(FileConvertor::deserialize).collect(Collectors.toList());
        if (CollUtil.isEmpty(files)) {
            throw new ServiceException("下载异常");
        }
        File first = CollUtil.getFirst(files);
        final String downloadFileName;
        try {
            if (files.size() == 1 && !first.isFolder()) {
                downloadFileName = first.getDescription().getFileName();
                BufferedInputStream inputStream = FileUtil.getInputStream(first.getResource().getPath());
                IoUtil.copy(inputStream, response.getOutputStream());
                inputStream.close();
            } else {
                downloadFileName = StrUtil.subBefore(first.getDescription().getFileName(), StrUtil.DOT, true) + ".zip";
                String commonPrefix = commonPath(files);
                List<File> fileListWithRealPath = fileRepository.getFileListWithRealPath(files);
                ZipOutputStream outputStream = new ZipOutputStream(response.getOutputStream());
                zip(outputStream, fileListWithRealPath, commonPrefix);
            }
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, URLUtil.encode(downloadFileName));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        return Response.ok();
    }

    private void zip(ZipOutputStream outputStream, List<File> fileListWithRealPath, String commonPrefix) throws IOException {
        for (File file : fileListWithRealPath) {
            if (!file.isFolder()) {
                outputStream.putNextEntry(new ZipEntry(StrUtil.removePrefix(file.getDescription().getRelativePath(),
                        commonPrefix)));
                BufferedInputStream inputStream = FileUtil.getInputStream(file.getResource().getPath());
                IoUtil.copy(inputStream, outputStream);
                inputStream.close();
            } else {
                outputStream.putNextEntry(new ZipEntry(StrUtil.removePrefix(file.getDescription().getRelativePath() + StrUtil.SLASH,
                        commonPrefix)));
            }
            outputStream.closeEntry();
        }
    }

    //获取公共路径
    private String commonPath(List<File> files) {
        if (CollUtil.isEmpty(files)) {
            return StrUtil.EMPTY;
        }

        File first = CollUtil.getFirst(files);
        File parent = first.newParentFolder();
        while (parent.getId() == null) {
            boolean isPre = true;
            for (File file : files) {
                if (!parent.isParentDir(file)) {
                    isPre = false;
                    break;
                }
            }
            if (isPre) {
                return parent.getDescription().getRelativePath();
            }
            parent = parent.newParentFolder();
        }
        return StrUtil.EMPTY;
    }
}
