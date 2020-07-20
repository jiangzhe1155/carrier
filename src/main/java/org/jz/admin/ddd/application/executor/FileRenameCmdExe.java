package org.jz.admin.ddd.application.executor;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileRenameCmd;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileRenameCmdExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileRenameCmd cmd) {


        String relativePath = params.getRelativePath();
        String fileName = FileUtil.getName(relativePath);
        String targetName = params.getTargetName();
        String targetPath = StrUtil.removeSuffix(relativePath, fileName) + targetName;

        fileRepository.getFileByRelativePath()

        if (sameNameFile != null) {
            targetName = newFileName(targetName);
        }

        List<TFile> files = fileMapper.selectList(new LambdaQueryWrapper<TFile>()
                .select(TFile::getRelativePath, TFile::getFileName, TFile::getId)
                .eq(TFile::getStatus, FileStatusEnum.CREATED)
                .and(wrapper -> wrapper
                        .eq(TFile::getRelativePath, relativePath)
                        .or()
                        .likeRight(TFile::getRelativePath, relativePath + StrUtil.SLASH)));

        String filePrePath = StrUtil.removeSuffix(relativePath, fileName) + targetName;
        for (TFile file : files) {
            String suf = StrUtil.removePrefix(file.getRelativePath(), relativePath);
            if (StrUtil.isEmpty(suf)) {
                //说明就是要改名的文件
                file.setFileName(targetName);
                file.setType(parseType(FileUtil.extName(targetName)));
            }

            file.setRelativePath(filePrePath + suf);
        }
        fileService.updateBatchById(files);

        return Response.ok();
    }
}
