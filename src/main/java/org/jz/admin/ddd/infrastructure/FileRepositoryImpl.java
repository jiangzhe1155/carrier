package org.jz.admin.ddd.infrastructure;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jz.admin.ddd.FileConvertor;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.entity.FileOrderByEnum;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author jz
 * @date 2020/07/14
 */
@Slf4j
@Repository
public class FileRepositoryImpl extends ServiceImpl<FileMapper, TFile> {

    @Autowired
    FileMapper fileMapper;

    private static final String LIMIT_ONE = "LIMIT 1";

    public Page<TFile> getFilePage(Long folderId, FileTypeEnum type, FileOrderByEnum orderBy, Boolean asc,
                                   Integer page, Integer pageSize) {

        LambdaQueryWrapper<TFile> wrapper = Wrappers.lambdaQuery();
        wrapper.select(TFile::getId, TFile::getFileName, TFile::getUpdateTime, TFile::getType, TFile::getSize
                , TFile::getRelativePath)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(TFile::getFolderId, folderId)
                .eq(type != null, TFile::getType, type)
                .orderByAsc(type == null, TFile::getType);

        if (orderBy != null && asc != null) {
            wrapper.orderBy(true, asc, orderBy.getKey());
        }

        return fileMapper.selectPage(new Page<>(page, pageSize), wrapper);
    }

    public File getFileByRelativePath(String relativePath, SFunction<TFile, ?>... columns) {
        LambdaQueryWrapper<TFile> wrapper = Wrappers.<TFile>lambdaQuery()
                .select(columns)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .eq(TFile::getRelativePath, relativePath)
                .last(LIMIT_ONE);
        TFile file = fileMapper.selectOne(wrapper);
        return FileConvertor.deserialize(file);
    }

    public List<File> getFilesByRelativePaths(List<String> relativePaths, SFunction<TFile, ?>... columns) {
        LambdaQueryWrapper<TFile> wrapper = Wrappers.<TFile>lambdaQuery()
                .select(columns)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .in(TFile::getRelativePath, relativePaths);
        List<TFile> files = fileMapper.selectList(wrapper);
        return files.stream().map(FileConvertor::deserialize).collect(Collectors.toList());
    }

    public boolean saveOrUpdate(File file) {
        TFile fileDO = FileConvertor.serialize(file);
        boolean success = saveOrUpdate(fileDO);
        if (success && file.getId() == null) {
            file.setId(fileDO.getId());
        }
        return success;
    }

    public File createDir(File rootDir, boolean touch) {

        if (rootDir.getId() != null) {
            return rootDir;
        }

        File fileByRelativePath = getFileByRelativePath(rootDir.getDescription().getRelativePath(), TFile::getId);
        if (fileByRelativePath != null) {
            if (touch) {
                return rootDir.setId(fileByRelativePath.getId());
            } else {
                rootDir.toNewFileName();
            }
        }

        do {
            Long folderId = createDir(rootDir.newParentFolder(), true).getId();
            rootDir.setFolderId(folderId).setStatus(FileStatusEnum.CREATED);
            if (saveOrUpdate(rootDir)) {
                return rootDir;
            } else {
                return createDir(rootDir, true);
            }
        } while (true);
    }

    public List<File> getFilesWithSubFilesByRelativePath(List<File> files, SFunction<TFile, ?>... columns) {
        LambdaQueryWrapper<TFile> wrapper = Wrappers.<TFile>lambdaQuery()
                .select(columns)
                .eq(TFile::getStatus, FileStatusEnum.CREATED);
        sqlFromDifferentType(wrapper, files);
        return fileMapper.selectList(wrapper).stream().map(FileConvertor::deserialize).collect(Collectors.toList());
    }

    public boolean saveOrUpdateBatch(List<File> files) {
        return saveOrUpdateBatch(files.stream().map(FileConvertor::serialize).collect(Collectors.toList()));
    }

    public void batchDeleteByRelativePath(List<File> files) {
        LambdaUpdateWrapper<TFile> wrapper = Wrappers.<TFile>lambdaUpdate()
                .set(TFile::getStatus, FileStatusEnum.DELETED)
                .eq(TFile::getStatus, FileStatusEnum.CREATED);

        sqlFromDifferentType(wrapper, files);
        fileMapper.update(null, wrapper);
    }

    private void sqlFromDifferentType(AbstractLambdaWrapper<TFile, ?> wrapper, List<File> files) {
        wrapper.and(w -> {
            for (File file : files) {
                String relativePath = file.getDescription().getRelativePath();
                if (file.isFolder()) {
                    w.or(w2 -> w2.eq(TFile::getRelativePath, relativePath)
                            .or()
                            .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH));
                } else {
                    w.or(w3 -> w3.eq(TFile::getRelativePath, relativePath));
                }
            }
        });

    }
}
