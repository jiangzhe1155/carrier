package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileListQry;
import org.jz.admin.ddd.infrastructure.FileRepositoryImpl;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author 江哲
 * @date 2020/07/14
 */
public class FileListQryExe {

    @Autowired
    FileRepositoryImpl fileRepository;


    public Response execute(FileListQry qry) {
        fileRepository.getFolderIdByRelativePath(qry.getRelativePath());


        List<TFile> fileList = fileRepository.getFileList(0L, null, false, TFile::getSize);
        return Response.ok(fileList);
    }
}
