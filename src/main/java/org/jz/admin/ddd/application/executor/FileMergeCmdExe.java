package org.jz.admin.ddd.application.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.jz.admin.aspect.ServiceException;
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
        File file = new File()
                .setFileName(cmd.getFilename())
                .setRelativePath(cmd.getRelativePath())
                .setSize(cmd.getTotalSize())
                .setResourceId(cmd.getStorageId())
                .setStatus(FileStatusEnum.CREATED);

        TFile fileFormDb = fileRepository.getFileByRelativePath(cmd.getRelativePath());

        // 判断是否有重名文件
        if (fileFormDb != null) {
            file.toNewFileName();
        }

        File parentFolder = fileRepository.createDir(new File().getParentFolder(), true);
        file.setFolderId(parentFolder.getId());

        if (!fileRepository.save(file)) {
            throw new ServiceException("创建文件失败");
        }

        return Response.ok();
    }

}
