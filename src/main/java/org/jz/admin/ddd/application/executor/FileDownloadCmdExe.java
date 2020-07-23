package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileDownloadCmd;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileDownloadCmdExe {
    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileDownloadCmd cmd) {
        List<File> files = fileRepository.getFilesByRelativePaths(cmd.getRelativePaths());

        return Response.failed();
    }
}
