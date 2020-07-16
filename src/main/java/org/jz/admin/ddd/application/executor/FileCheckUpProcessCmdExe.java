package org.jz.admin.ddd.application.executor;

import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileCheckUpProcessCmd;
import org.jz.admin.ddd.domain.FileName;
import org.jz.admin.ddd.domain.FileResource;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.TFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileCheckUpProcessCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    @Autowired
    RedisTemplate redisTemplate;

    public Response execute(FileCheckUpProcessCmd cmd) {

        FileResource resource = new FileResource()
                .setIdentifier(cmd.getIdentifier())
                .setFileName(FileName.valueOf(cmd.getFilename()));
        TFileStore fileStoreDO = fileRepository.getResourceByIdentifier(cmd.getIdentifier());
        resource.setId(fileStoreDO.getId()).setStatus(fileStoreDO.getStatus());

        FileCheckUpProcessCO fileCheckUpProcessCO = new FileCheckUpProcessCO();
        if (resource.isCreated()) {
            return Response.ok(fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(true));
        }

        if (resource.isCreating()) {
            Set<Integer> members = redisTemplate.opsForSet().members(cmd.getIdentifier());
            return Response.ok(fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(false).setUploaded(members));
        }

        resource.generateRealPath().setStatus(FileStatusEnum.NEW);
        if (!fileRepository.save(resource)) {
            throw new ServiceException("未知错误");
        }
        return Response.ok(fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(false));
    }
}
