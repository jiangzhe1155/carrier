package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileChunkUploadCmd;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileChunkUploadCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileChunkUploadCmd cmd) {
        return null;
    }
}
