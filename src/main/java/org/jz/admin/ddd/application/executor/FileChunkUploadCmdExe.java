package org.jz.admin.ddd.application.executor;

import cn.hutool.core.util.BooleanUtil;
import org.jz.admin.aspect.ServiceException;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileChunkUploadCmd;
import org.jz.admin.ddd.domain.FileResource;
import org.jz.admin.ddd.infrastructure.FileResourceRepositoryImpl;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.TFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileChunkUploadCmdExe {

    @Autowired
    FileResourceRepositoryImpl fileResourceRepository;

    @Autowired
    RedisTemplate redisTemplate;

    public Response execute(MultipartFile file, FileChunkUploadCmd cmd) {

        FileResource resource =
                new FileResource().setIdentifier(cmd.getIdentifier()).setChunkNumber(cmd.getChunkNumber()).setChunkSize(cmd.getChunkSize());

        TFileStore fileStoreDO = fileResourceRepository.getResourceByIdentifier(cmd.getIdentifier());

        if (fileStoreDO == null) {
            throw new ServiceException("上传文件失败,源文件缺失");
        }
        resource.setId(fileStoreDO.getId()).setPath(fileStoreDO.getPath()).setStatus(fileStoreDO.getStatus());

        if (BooleanUtil.isFalse(redisTemplate.opsForSet().isMember(cmd.getIdentifier(), cmd.getChunkNumber()))) {
            resource.rangeWrite(file);
            FileStatusEnum status = resource.todoUpdateStatus();
            if (!status.equals(resource.getStatus())) {
                //需要更新状态
                fileResourceRepository.save(new FileResource().setId(resource.getId()).setStatus(status));
            }
            //添加缓存
            redisTemplate.opsForSet().add(cmd.getIdentifier(), cmd.getChunkNumber());
        }

        return Response.ok();
    }
}