package org.jz.admin.ddd.application.executor;

import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileCheckUpProgressCmd;
import org.jz.admin.ddd.domain.FileName;
import org.jz.admin.ddd.domain.FileResource;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.ddd.infrastructure.FileResourceRepositoryImpl;
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
    FileResourceRepositoryImpl fileResourceRepository;

    @Autowired
    RedisTemplate redisTemplate;

    public Response execute(FileCheckUpProgressCmd cmd) {
        FileResource resource = new FileResource()
                .setIdentifier(cmd.getIdentifier())
                .setFileName(cmd.getFilename());
        TFileStore fileStoreDO = fileResourceRepository.getResourceByIdentifier(cmd.getIdentifier());
        if (fileStoreDO != null) {
            resource.setId(fileStoreDO.getId()).setStatus(fileStoreDO.getStatus());
        }


        FileCheckUpProcessCO fileCheckUpProcessCO = new FileCheckUpProcessCO();
        if (resource.isCreated()) {
            return Response.ok(fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(true));
        }

        if (resource.isCreating()) {
            Set<Integer> members = redisTemplate.opsForSet().members(cmd.getIdentifier());
            return Response.ok(fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(false).setUploaded(members));
        }

        resource.generateRealPath().setStatus(FileStatusEnum.NEW);
        if (!fileResourceRepository.save(resource)) {
            throw new ServiceException("未知错误");
        }

        return Response.ok(fileCheckUpProcessCO.setId(resource.getId()).setSkipUpload(false));
    }
}
