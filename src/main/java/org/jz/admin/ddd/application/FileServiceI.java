package org.jz.admin.ddd.application;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.*;
import org.jz.admin.ddd.application.executor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author jz
 * @date 2020/07/14
 */
@Service
public class FileServiceI {

    @Autowired
    FileDeleteCmdExe fileDeleteCmdExe;
    @Autowired
    FileMakeDirCmdExe fileMakeDirCmdExe;
    @Autowired
    FileRenameCmdExe fileRenameCmdExe;
    @Autowired
    FileCheckUpProcessCmdExe fileCheckUpProgressCmdExe;
    @Autowired
    FileChunkUploadCmdExe fileChunkUploadCmdExe;
    @Autowired
    FileMergeCmdExe fileMergeCmdExe;
    @Autowired
    FileCopyCmdExe fileCopyCmdExe;
    @Autowired
    FileMoveOrCopyCmdExe fileMoveOrCopyCmdExe;
    @Autowired
    FileDownloadCmdExe fileDownloadCmdExe;
    @Autowired
    FileListQryExe fileListQryExe;

    public Response delete(FileDeleteCmd cmd) {
        return fileDeleteCmdExe.execute(cmd);
    }


    public Response makeDir(FileMakeDirCmd cmd) {
        return fileMakeDirCmdExe.execute(cmd);
    }


    public Response rename(FileRenameCmd cmd) {
        return fileRenameCmdExe.execute(cmd);
    }


    public Response checkUpProgress(FileCheckUpProgressCmd cmd) {
        return fileCheckUpProgressCmdExe.execute(cmd);
    }


    public Response chunkUpload(MultipartFile file, FileChunkUploadCmd cmd) {
        return fileChunkUploadCmdExe.execute(file, cmd);
    }


    public Response merge(FileMergeCmd cmd) {
        return fileMergeCmdExe.execute(cmd);
    }

    public Response copy(FileMoveOrCopyCmd cmd) {
        return fileMoveOrCopyCmdExe.execute(cmd, false);
    }

    public Response move(FileMoveOrCopyCmd cmd) {
        return fileMoveOrCopyCmdExe.execute(cmd, true);
    }

    public Response download(FileDownloadCmd cmd) {
        return fileDownloadCmdExe.execute(cmd);
    }

    public Response list(FileListQry qry) {
        return fileListQryExe.execute(qry);
    }

}