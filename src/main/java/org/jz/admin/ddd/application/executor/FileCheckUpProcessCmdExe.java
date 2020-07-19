package org.jz.admin.ddd.application.executor;

import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileCheckUpProgressCmd;
import org.jz.admin.ddd.domain.FileResource;
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
                .setFileName(cmd.getFilename())
                .setStatus(FileStatusEnum.NEW);

        TFileStore fileStoreDO = fileResourceRepository.getResourceByIdentifier(cmd.getIdentifier());

        if (fileStoreDO != null) {
            resource.setId(fileStoreDO.getId()).setStatus(fileStoreDO.getStatus());
        }

        FileCheckUpProgressCO progressResponse = new FileCheckUpProgressCO();

        if (resource.isCreated()) {
            return Response.ok(progressResponse.setId(resource.getId()).setSkipUpload(true));
        }

        if (resource.getId() != null && !resource.isCreated()) {
            Set<Integer> members = redisTemplate.opsForSet().members(cmd.getIdentifier());
            return Response.ok(progressResponse.setId(resource.getId()).setSkipUpload(false).setUploaded(members));
        }

        resource.generateRealPath();
        if (!fileResourceRepository.save(resource)) {
            throw new ServiceException("保存失败");
        }

        return Response.ok(progressResponse.setId(resource.getId()).setSkipUpload(false));
    }
}
