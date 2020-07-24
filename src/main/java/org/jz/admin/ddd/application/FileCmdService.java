package org.jz.admin.ddd.application;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.*;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author jz
 * @date 2020/07/24
 */
public interface FileCmdService {

    Response list(FileListQry query);


    Response delete(FileDeleteCmd cmd);


    Response makeDir(FileMakeDirCmd cmd);


    Response rename(FileRenameCmd cmd);


    Response checkUpProgress(FileCheckUpProgressCmd cmd);


    Response chunkUpload(MultipartFile file, FileChunkUploadCmd cmd);


    Response merge(FileMergeCmd cmd);


    Response copy(FileMoveOrCopyCmd cmd);


    Response move(FileMoveOrCopyCmd cmd);


    void download(FileDownloadCmd cmd);

}
