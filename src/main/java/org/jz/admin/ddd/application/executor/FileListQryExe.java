package org.jz.admin.ddd.application.executor;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileListQry;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author 江哲
 * @date 2020/07/14
 */
public class FileListQryExe {

    @Autowired
    FileRepositoryImpl fileRepository;

    public Response execute(FileListQry qry) {
        Long folderId = fileRepository.getFolderIdByRelativePath(qry.getRelativePath());
        Page<TFile> filePage = fileRepository.getFilePage(folderId, qry.getFileType(), TFile::getFileName,
                qry.getAsc(), qry.getPage(), qry.getPageSize());
        return Response.ok(filePage);
    }
}
