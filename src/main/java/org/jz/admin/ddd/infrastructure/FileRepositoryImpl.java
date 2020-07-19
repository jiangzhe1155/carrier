package org.jz.admin.ddd.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author jz
 * @date 2020/07/14
 */

@Repository
public class FileRepositoryImpl extends ServiceImpl<FileMapper, TFile> {

    @Autowired
    FileMapper fileMapper;

    private static final String LIMIT_ONE = "LIMIT 1";
    private String relativePath;


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

    public TFile getFileByRelativePath(String relativePath, SFunction<TFile, ?>... columns) {
        LambdaQueryWrapper<TFile> wrapper = Wrappers.<TFile>lambdaQuery()
                .select(columns)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(TFile::getRelativePath, relativePath)
                .last(LIMIT_ONE);
        return fileMapper.selectOne(wrapper);
    }

    public boolean save(File file) {
        TFile fileDO = new TFile()
                .setId(file.getId())
                .setStatus(FileStatusEnum.CREATED)
                .setType(file.getType())
                .setFolderId(file.getFolderId())
                .setRelativePath(file.getRelativePath())
                .setFileName(file.getFileName())
                .setSize(file.getSize());
        boolean success = saveOrUpdate(fileDO);
        if (file.getId() == null) {
            file.setId(fileDO.getId());
        }
        return success;
    }

    public File createDir(File rootDir, boolean touch) {
        if (rootDir.getId() != null) {
            return rootDir;
        }
        // 判断是否有重名文件
        TFile fileByRelativePath = getFileByRelativePath(rootDir.getRelativePath());
        if (fileByRelativePath != null) {
            if (touch) {
                return rootDir.setId(fileByRelativePath.getId());
            } else {
                rootDir.toNewFileName();
            }
        }

        Long folderId = createDir(rootDir.getParentFolder(), true).getId();
        rootDir.setFolderId(folderId).setStatus(FileStatusEnum.CREATED);
        save(rootDir);
        return rootDir;
    }
}
