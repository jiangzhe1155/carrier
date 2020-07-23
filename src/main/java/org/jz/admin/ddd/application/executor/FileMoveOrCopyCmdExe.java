package org.jz.admin.ddd.application.executor;

import cn.hutool.core.util.StrUtil;
import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileMoveOrCopyCmd;
import org.jz.admin.ddd.application.dto.co.FileMoveOrCopyCo;
import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileMoveOrCopyCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileMoveOrCopyCmd cmd, boolean move) {
        Map<String, List<String>> targetPathMap =
                cmd.getFileList().stream().collect(Collectors.groupingBy(FileMoveOrCopyCo::getTargetPath,
                        Collectors.mapping(FileMoveOrCopyCo::getRelativePath, Collectors.toList())));

        for (Map.Entry<String, List<String>> entry : targetPathMap.entrySet()) {
            File targetFolder = new File().setDescription(new Description(entry.getKey(), true));
            if (targetFolder.getId() == null) {
                targetFolder = fileRepository.getFileByRelativePath(targetFolder.getDescription().getRelativePath());
            }
            if (targetFolder == null) {
                throw new ServiceException("找不到目标目录");
            }

            List<File> moveFiles = fileRepository.getFilesByRelativePaths(entry.getValue());
            for (File moveFile : moveFiles) {
                if (moveFile.isSameOrParentDir(targetFolder)) {
                    throw new ServiceException("不能复制/移动到自身目录或子目录");
                }
            }

            List<File> filesWithSubFiles = fileRepository.getFilesWithSubFilesByRelativePath(moveFiles);

            for (File moveFile : moveFiles) {
                File targetFile = targetFolder.newSubFile(moveFile.getDescription().getFileName(), moveFile.isFolder());
                if (fileRepository.getFileByRelativePath(targetFile.getDescription().getRelativePath()) != null) {
                    targetFile.toNewFileName();
                }
                for (File fileReadyToUpdate : filesWithSubFiles) {
                    if (fileReadyToUpdate.getId().equals(moveFile.getId())) {
                        fileReadyToUpdate.setDescription(targetFile.getDescription());
                        fileReadyToUpdate.setFolderId(targetFolder.getId());
                    } else if (moveFile.isParentDir(fileReadyToUpdate)) {
                        String newRelativePath =
                                targetFile.getDescription().getRelativePath() + StrUtil.removePrefix(fileReadyToUpdate.getDescription().getRelativePath(), moveFile.getDescription().getRelativePath());
                        fileReadyToUpdate.setDescription(new Description(newRelativePath));
                    }
                }
            }

            if (!move) {
                filesWithSubFiles.forEach(f -> f.setId(null));
            }
            fileRepository.saveOrUpdateBatch(filesWithSubFiles);
        }
        return Response.ok();
    }
}
