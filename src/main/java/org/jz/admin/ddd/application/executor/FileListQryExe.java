package org.jz.admin.ddd.application.executor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileListQry;
import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileListQryExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileListQry qry) {
        File parentFolder = new File().setDescription(new Description(qry.getRelativePath(), true));
        if (parentFolder.getId() == null) {
            File fileByRelativePath = fileRepository.getFileByRelativePath(qry.getRelativePath());
            if (fileByRelativePath != null) {
                parentFolder.setId(fileByRelativePath.getId());
            }
        }
        Page<TFile> filePage = fileRepository.getFilePage(parentFolder.getId(), qry.getFileType(),
                qry.getOrder().getKey(), qry.getAsc(), qry.getPage(), qry.getPageSize());
        return Response.ok(filePage);
    }
}
