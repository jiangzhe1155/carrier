package org.jz.admin.ddd.infrastructure;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class FileRepositoryImpl {

    @Autowired
    FileMapper fileMapper;

    @Autowired
    FileStoreMapper fileStoreMapper;

    public static final String LIMIT_ONE = "LIMIT 1";
    private static final long ROOT_FOLDER_ID = 0L;

    public Page<TFile> getFilePage(Long folderId, FileTypeEnum type, SFunction<TFile, ?> orderBy, Boolean asc,
                                   Integer page, Integer pageSize) {

        LambdaQueryWrapper<TFile> wrapper = Wrappers.lambdaQuery();
        wrapper.select(TFile::getId, TFile::getFileName, TFile::getUpdateTime, TFile::getType, TFile::getSize
                , TFile::getRelativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(TFile::getFolderId, folderId)
                .eq(type != null, TFile::getType, type)
                .orderByAsc(type == null, TFile::getType)
                .orderBy(true, asc, orderBy);
        return fileMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    public Long getFolderIdByRelativePath(String relativePath) {
        if (StrUtil.isEmpty(relativePath)) {
            return ROOT_FOLDER_ID;
        }
        LambdaQueryWrapper<TFile> wrapper = Wrappers.<TFile>lambdaQuery()
                .select(TFile::getId)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(TFile::getRelativePath, relativePath)
                .last(LIMIT_ONE);

        return fileMapper.selectOne(wrapper).getId();
    }

    public TFileStore getResourceByIdentifier(String identifier) {

        TFileStore fileStoreDO = fileStoreMapper.selectOne(new LambdaQueryWrapper<TFileStore>()
                .select(TFileStore::getId, TFileStore::getStatus)
                .ne(TFileStore::getStatus, FileStatusEnum.DELETED)
                .eq(TFileStore::getIdentifier, identifier)
                .last(LIMIT_ONE)
        );

        return fileStoreDO;
    }

    public boolean save(FileResource resource) {
        TFileStore file = new TFileStore();
        BeanUtil.copyProperties(resource, file);
        int effectRow;
        if (file.getId() == null) {
            effectRow = fileStoreMapper.insert(file);
            resource.setId(file.getId());
        } else {
            effectRow = fileStoreMapper.updateById(file);
        }

        return effectRow > 0;
    }
}
