package org.jz.admin.ddd.application.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileMergeCmd;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.domain.FileName;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
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

    public Response execute(FileMergeCmd cmd) {

        File file = new File().setFileName(cmd.getFilename())
                .setRelativePath(cmd.getRelativePath())
                .setSize(cmd.getTotalSize()).setResourceId(cmd.getStorageId());

        TFile fileDO = fileRepository.getFileByRelativePath(cmd.getRelativePath());
        // 判断是否有重名文件
        if (fileDO != null) {
            //存在重名
            file.toNewFileName();
        }

        File parentFolder = createDir(new File().getParentFolder(), true);
        file.setFolderId(parentFolder.getId()).setStatus(FileStatusEnum.CREATED);

        // 开始创建文件
        fileRepository.save(file);
        return Response.ok();
    }

    private File createDir(File rootDir, boolean touch) {
        if (rootDir.getId() != null) {
            return rootDir;
        }
        // 判断是否有重名文件
        TFile fileByRelativePath = fileRepository.getFileByRelativePath(rootDir.getRelativePath());
        if (fileByRelativePath != null) {
            if (touch) {
                return rootDir.setId(fileByRelativePath.getId());
            } else {
                rootDir.toNewFileName();
            }
        }

        Long folderId = createDir(rootDir.getParentFolder(), true).getId();
        rootDir.setFolderId(folderId).setStatus(FileStatusEnum.CREATED);
        fileRepository.save(rootDir);
        return rootDir;
    }
}
