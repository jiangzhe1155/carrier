package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileCheckUpProcessCmd;
import org.jz.admin.ddd.domain.FileName;
import org.jz.admin.ddd.domain.FileResource;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.TFileStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author 江哲
 * @date 2020/07/14
 */
public class FileCheckUpProcessCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;


    public Response execute(FileCheckUpProcessCmd cmd) {


        FileResource resource =
                new FileResource().setIdentifier(cmd.getIdentifier())
                        .setName(FileName.valueOf(cmd.getFilename()));
        TFileStore fileStoreDO = fileRepository.getResourceByIdentifier(cmd.getIdentifier());
        resource.setId(fileStoreDO.getId()).setStatus(fileStoreDO.getStatus());


        FileCheckUpProcessCO fileCheckUpProcessCO = new FileCheckUpProcessCO();
        if (resource.isCreated()) {
            fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(true);
            return Response.ok(fileCheckUpProcessCO);
        }

        if (resource.isCreating()) {
            fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(false).setUploaded(Collections.emptyList());
            return Response.ok(fileCheckUpProcessCO);
        }

        resource.setStatus(FileStatusEnum.NEW);

//        String realFilePath = fileUtilService.absPath(null, newFileName(params.getFilename()));
//        target = new TFileStore()
//                .setIdentifier(params.getIdentifier())
//                .setPath(realFilePath)
//                .setStatus(FileStatusEnum.NEW);
//        fileStoreMapper.insert(target);
        return null;
    }
}
