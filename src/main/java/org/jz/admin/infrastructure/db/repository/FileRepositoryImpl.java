package org.jz.admin.infrastructure.db.repository;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jz.admin.infrastructure.db.convertor.FileConvertor;
import org.jz.admin.domain.File;
import org.jz.admin.common.enums.FileOrderByEnum;
import org.jz.admin.common.enums.FileStatusEnum;
import org.jz.admin.common.enums.FileTypeEnum;
import org.jz.admin.infrastructure.db.dataobject.FileDO;
import org.jz.admin.infrastructure.db.FileMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jz
 * @date 2020/07/14
 */
@Slf4j
@Repository
public class FileRepositoryImpl extends ServiceImpl<FileMapper, FileDO> {

    public Page<FileDO> filePage(Long folderId, FileTypeEnum type, FileOrderByEnum orderBy, Boolean asc, Integer page,
                                 Integer pageSize) {
        LambdaQueryWrapper<FileDO> wrapper = Wrappers.<FileDO>lambdaQuery()
                .eq(FileDO::getStatus, FileStatusEnum.CREATED)
                .eq(FileDO::getFolderId, folderId)
                .eq(type != null, FileDO::getType, type)
                .orderByAsc(type == null, FileDO::getType);

        if (orderBy != null && asc != null) {
            wrapper.orderBy(true, asc, orderBy.getKey());
        }
        return page(new Page<>(page, pageSize), wrapper);
    }


    public File getFileByRelativePath(String relativepath) {
        LambdaQueryWrapper<FileDO> wrapper = Wrappers.<FileDO>lambdaQuery()
                .eq(FileDO::getStatus, FileStatusEnum.CREATED)
                .eq(FileDO::getRelativePath, relativepath);
        FileDO file = getOne(wrapper);
        return FileConvertor.deserialize(file);
    }

    public List<File> getFilesByRelativePaths(List<String> relativePaths) {
        LambdaQueryWrapper<FileDO> wrapper = Wrappers.<FileDO>lambdaQuery()
                .eq(FileDO::getStatus, FileStatusEnum.CREATED)
                .in(FileDO::getRelativePath, relativePaths);
        List<FileDO> files = baseMapper.selectList(wrapper);
        return files.stream().map(FileConvertor::deserialize).collect(Collectors.toList());
    }

    public boolean saveOrUpdate(File file) {
        FileDO fileDO = FileConvertor.serialize(file);
        boolean success = saveOrUpdate(fileDO);
        if (success && file != null && file.getId() == null) {
            file.setId(fileDO.getId());
        }
        return success;
    }

    public File createDir(File rootDir, boolean touch) {
        if (rootDir.getId() != null) {
            return rootDir;
        }

        File fileByRelativePath = getFileByRelativePath(rootDir.getRelativePath());
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

    public List<File> getFilesWithSubFilesByRelativePath(List<File> files, SFunction<FileDO, ?>... columns) {
        LambdaQueryWrapper<FileDO> wrapper = Wrappers.<FileDO>lambdaQuery()
                .select(columns)
                .eq(FileDO::getStatus, FileStatusEnum.CREATED);
        sqlFromDifferentType(wrapper, files);
        return list(wrapper).stream().map(FileConvertor::deserialize).collect(Collectors.toList());
    }

    public boolean saveOrUpdateBatch(List<File> files) {
        List<FileDO> filesDO = files.stream().map(FileConvertor::serialize).collect(Collectors.toList());
        boolean success = saveOrUpdateBatch(filesDO);
        for (int i = 0; i < files.size(); i++) {
            files.get(i).setId(filesDO.get(i).getId());
        }
        return success;
    }

    public boolean batchDeleteByRelativePath(List<File> files) {
        LambdaUpdateWrapper<FileDO> wrapper = Wrappers.<FileDO>lambdaUpdate()
                .set(FileDO::getStatus, FileStatusEnum.DELETED)
                .eq(FileDO::getStatus, FileStatusEnum.CREATED);
        sqlFromDifferentType(wrapper, files);
        return update(null, wrapper);
    }

    private void sqlFromDifferentType(AbstractLambdaWrapper<FileDO, ?> wrapper, List<File> files) {
        wrapper.and(andWrapper -> {
            for (File file : files) {
                String relativePath = file.getRelativePath();
                andWrapper.or(w -> {
                    w.eq(FileDO::getRelativePath, relativePath);
                    if (file.isFolder()) {
                        w.or().likeRight(FileDO::getRelativePath, relativePath + StrUtil.SLASH);
                    }
                });
            }
        });
    }

    public List<File> getFileListWithSourcePath(List<File> files) {
        LambdaQueryWrapper<FileDO> wrapper = Wrappers.<FileDO>lambdaQuery();
        sqlFromDifferentType(wrapper, files);
        List<FileDO> fileDO = baseMapper.getFileListWithSourcePath(wrapper);
        return fileDO.stream().map(FileConvertor::deserialize).collect(Collectors.toList());
    }
}
