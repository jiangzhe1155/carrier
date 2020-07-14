package org.jz.admin.ddd.application;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jz
 * @date 2020/07/14
 */
@Component
public class FileCmdServiceImpl {


    public Response delete(@RequestBody FileDeleteCmd cmd) {

        return null;
    }


    public Response makeDir(@RequestBody FileMakeDirCmd cmd) {

        return null;
    }


    public Response rename(@RequestBody FileRenameCmd cmd) {

        return null;
    }


    public Response checkUpProcess(FileCheckUpProcessCmd cmd) {

        return null;
    }


    public Response chunkUpload(FileChunkUploadCmd cmd) {

        return null;
    }


    public Response merge(@RequestBody FileMergeCmd cmd) {

        return null;
    }


    public Response copy(@RequestBody FileCopyCmd cmd) {

        return null;
    }

    public Response move(@RequestBody FileMoveCmd cmd) {

        return null;
    }


    public Response download(@RequestBody FileDownloadCmd cmd) {

        return null;
    }

}
