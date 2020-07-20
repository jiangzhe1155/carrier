package org.jz.admin.ddd.application.executor;

import cn.hutool.core.util.StrUtil;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileMakeDirCmd;
import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.ddd.infrastructure.FileResourceRepositoryImpl;
import org.jz.admin.entity.FileTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileMakeDirCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileMakeDirCmd cmd) {
        File folder = new File().setDescription(new Description(cmd.getRelativePath(), true));
        fileRepository.createDir(folder, false);
        return Response.ok();
    }
}
