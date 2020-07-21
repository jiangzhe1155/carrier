package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileDeleteCmd;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        List<File> deleteFiles =
                fileRepository.getFilesWithSubFilesByRelativePath(filesByRelativePaths, TFile::getId);

        fileRepository.batchDelete(deleteFiles.stream().map(File::getId).collect(Collectors.toList()));

        return null;
    }
}
