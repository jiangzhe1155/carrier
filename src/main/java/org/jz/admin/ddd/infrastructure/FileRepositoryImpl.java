package org.jz.admin.ddd.infrastructure;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jz.admin.ddd.FileConvertor;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

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
        File fileByRelativePath = getFileByRelativePath(rootDir.getDescription().getRelativePath(), TFile::getId);
        if (fileByRelativePath != null) {
            if (touch) {
                return rootDir.setId(fileByRelativePath.getId());
            } else {
                rootDir.toNewFileName();
            }
        }
        Long folderId = createDir(rootDir.newParentFolder(), true).getId();
        rootDir.setFolderId(folderId).setStatus(FileStatusEnum.CREATED);
        saveOrUpdate(rootDir);
        return rootDir;
    }


    public List<File> getFilesWithSubFilesByRelativePath(List<File> files, SFunction<TFile, ?>... columns) {
        LambdaQueryWrapper<TFile> wrapper = Wrappers.<TFile>lambdaQuery()
                .select(columns)
                .eq(TFile::getStatus, FileStatusEnum.CREATED);
        sqlFromDifferentType(wrapper, files);
        return fileMapper.selectList(wrapper).stream().map(FileConvertor::deserialize).collect(Collectors.toList());
    }

    public void moveFiles(List<File> moveFiles, File targetFile) {

    }

    public boolean saveOrUpdateBatch(List<File> files) {
        return saveOrUpdateBatch(files.stream().map(FileConvertor::serialize).collect(Collectors.toList()));
    }

    public void batchDelete(List<File> files) {
        LambdaUpdateWrapper<TFile> wrapper = Wrappers.<TFile>lambdaUpdate()
                .set(TFile::getStatus, FileStatusEnum.DELETED)
                .eq(TFile::getStatus, FileStatusEnum.CREATED);

        sqlFromDifferentType(wrapper, files);

        fileMapper.update(null, wrapper);
    }

    private void sqlFromDifferentType(AbstractLambdaWrapper<TFile, ?> wrapper, List<File> files) {
        for (File file : files) {
            if (file.isFolder()) {
                wrapper.and(w -> w
                        .eq(TFile::getRelativePath, relativePath)
                        .or()
                        .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH));
            } else {
                wrapper.eq(TFile::getRelativePath, relativePath);
            }
        }
    }
}
