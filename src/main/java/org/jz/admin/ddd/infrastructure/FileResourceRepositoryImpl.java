package org.jz.admin.ddd.infrastructure;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.domain.FileResource;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.entity.TFileStore;
import org.jz.admin.mapper.FileMapper;
import org.jz.admin.mapper.FileStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jz
 * @date 2020/07/14
 */

@Repository
public class FileResourceRepositoryImpl extends ServiceImpl<FileStoreMapper, TFileStore> {


    @Autowired
    FileStoreMapper fileStoreMapper;

    public static final String LIMIT_ONE = "LIMIT 1";


    public TFileStore getResourceByIdentifier(String identifier) {
        TFileStore fileStoreDO = fileStoreMapper.selectOne(new LambdaQueryWrapper<TFileStore>()
                .ne(TFileStore::getStatus, FileStatusEnum.DELETED)
                .eq(TFileStore::getIdentifier, identifier)
                .last(LIMIT_ONE));
        return fileStoreDO;
    }

    public boolean save(FileResource resource) {
        TFileStore file = new TFileStore();
        BeanUtil.copyProperties(resource, file);
        return saveOrUpdate(file);
    }


}
