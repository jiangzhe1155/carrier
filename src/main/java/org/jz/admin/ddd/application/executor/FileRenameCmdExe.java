package org.jz.admin.ddd.application.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileRenameCmd;
import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.jz.admin.entity.FileTypeEnum.parseType;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileRenameCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileRenameCmd cmd) {
        TFile originFile = fileRepository.getFileByRelativePath(cmd.getRelativePath());
        if (originFile == null) {
            return Response.failed();
        }
        File file = new File()
                .setDescription(new Description(originFile.getRelativePath(),
                        FileTypeEnum.isFolder(originFile.getType())))
                .setId(originFile.getId());

        String targetPath =
                StrUtil.removeSuffix(originFile.getRelativePath(), originFile.getFileName()) + cmd.getTargetName();
        TFile targetFromDb = fileRepository.getFileByRelativePath(targetPath, TFile::getId);

        if (targetFromDb != null) {
            file.toNewFileName();
        }

        if (!file.isFolder()) {
            fileRepository.update(file);
        } else {
            List<TFile> filesWithSubFiles =
                    fileRepository.getFilesWithSubFilesByRelativePath(Collections.singletonList(file), TFile::getId,
                            TFile::getRelativePath);
            for (TFile filesWithSubFile : filesWithSubFiles) {
                String suf = StrUtil.removePrefix(filesWithSubFile.getRelativePath(), cmd.getRelativePath());
//                filesWithSubFile.setRelativePath(file.getRelativePath() + suf);
            }
            fileRepository.saveOrUpdateBatch(filesWithSubFiles);
        }
        return Response.ok();
    }
}
