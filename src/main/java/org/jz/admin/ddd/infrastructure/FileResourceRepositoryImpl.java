package org.jz.admin.ddd.infrastructure;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jz.admin.ddd.domain.FileResource;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.TFileResource;
import org.jz.admin.mapper.FileStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jz
 * @date 2020/07/14
 */

@Repository
public class FileResourceRepositoryImpl extends ServiceImpl<FileStoreMapper, TFileResource> {

    @Autowired
    FileStoreMapper fileStoreMapper;

    public TFileResource getResourceByIdentifier(String identifier) {
        TFileResource fileStoreDO = fileStoreMapper.selectOne(new LambdaQueryWrapper<TFileResource>()
                .ne(TFileResource::getStatus, FileStatusEnum.DELETED)
                .eq(TFileResource::getIdentifier, identifier));
        return fileStoreDO;
    }

    public boolean save(FileResource resource) {
        TFileResource file = new TFileResource();
        BeanUtil.copyProperties(resource, file);
        boolean success = saveOrUpdate(file);
        if (resource.getId() == null) {
            resource.setId(file.getId());
        }
        return success;
    }


}
