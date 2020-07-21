package org.jz.admin.ddd.application.executor;

import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileMergeCmd;
import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileMergeCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public  Response execute(FileMergeCmd cmd) {
        File file = new File()
                .setDescription(new Description(cmd.getRelativePath(), false))
                .setSize(cmd.getTotalSize())
                .setResourceId(cmd.getStorageId())
                .setSize(cmd.getTotalSize())
                .setStatus(FileStatusEnum.CREATED);
        // 判断是否有重名文件
        if (fileRepository.getFileByRelativePath(file.getDescription().getRelativePath(), TFile::getId) != null) {
            file.toNewFileName();
        }

        File parentFolder = fileRepository.createDir(file.newParentFolder(), true);
        file.setFolderId(parentFolder.getId());

        if (!fileRepository.saveOrUpdate(file)) {
            throw new ServiceException("创建文件失败");
        }

        return Response.ok(file);
    }

}
