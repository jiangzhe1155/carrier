package org.jz.admin.ddd.application.executor;

import cn.hutool.core.util.StrUtil;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileRenameCmd;
import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileRenameCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileRenameCmd cmd) {
        File file = fileRepository.getFileByRelativePath(cmd.getRelativePath());
        if (file == null) {
            return Response.failed();
        }
        Description originFileDescription = file.getDescription();
        file.rename(cmd.getTargetName());
        if (fileRepository.getFileByRelativePath(file.getDescription().getRelativePath(), TFile::getId) != null) {
            file.toNewFileName();
        }
        if (!file.isFolder()) {
            fileRepository.saveOrUpdate(file);
        } else {
            List<File> filesWithSubFiles =
                    fileRepository.getFilesWithSubFilesByRelativePath(Collections.singletonList(new File().setDescription(originFileDescription)));
            for (File filesWithSubFile : filesWithSubFiles) {
                String suf = StrUtil.removePrefix(filesWithSubFile.getDescription().getRelativePath(),
                        originFileDescription.getRelativePath());
                filesWithSubFile.setDescription(new Description(file.getDescription().getRelativePath()));
            }
            fileRepository.saveOrUpdateBatch(filesWithSubFiles);
        }
        return Response.ok();
    }
}
