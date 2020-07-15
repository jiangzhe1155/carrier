package org.jz.admin.ddd.infrastructure;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jz
 * @date 2020/07/14
 */
public class FileRepositoryImpl {

    @Autowired
    FileMapper fileMapper;

    public static final Long ROOT_FOLDER_ID = 0L;

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
                .last("limit 1");

        return fileMapper.selectOne(wrapper).getId();
    }

}
