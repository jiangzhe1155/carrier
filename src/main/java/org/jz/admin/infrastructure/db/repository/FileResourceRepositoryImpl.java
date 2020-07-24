package org.jz.admin.infrastructure.db.repository;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jz.admin.domain.FileResource;
import org.jz.admin.common.enums.FileStatusEnum;
import org.jz.admin.infrastructure.db.dataobject.FileResourceDO;
import org.jz.admin.infrastructure.db.FileStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jz
 * @date 2020/07/14
 */

@Repository
public class FileResourceRepositoryImpl extends ServiceImpl<FileStoreMapper, FileResourceDO> {

    @Autowired
    FileStoreMapper fileStoreMapper;

    public FileResourceDO getResourceIdByIdentifier(String identifier) {
        LambdaQueryWrapper<FileResourceDO> wrapper = Wrappers.<FileResourceDO>lambdaQuery()
                .ne(FileResourceDO::getStatus, FileStatusEnum.DELETED)
                .eq(FileResourceDO::getIdentifier, identifier);
        return baseMapper.selectOne(wrapper);
    }

    public boolean save(FileResource resource) {
        FileResourceDO file = new FileResourceDO();
        BeanUtil.copyProperties(resource, file);
        boolean success = saveOrUpdate(file);
        if (resource.getId() == null) {
            resource.setId(file.getId());
        }
        return success;
    }


}
