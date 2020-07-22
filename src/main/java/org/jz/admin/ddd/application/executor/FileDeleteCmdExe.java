package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileDeleteCmd;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileDeleteCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileDeleteCmd cmd) {
        List<File> filesByRelativePaths = fileRepository.getFilesByRelativePaths(cmd.getRelativePaths());
        fileRepository.batchDeleteByRelativePath(filesByRelativePaths);
        return Response.ok();
    }
}
