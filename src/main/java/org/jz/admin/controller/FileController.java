package org.jz.admin.controller;

import lombok.extern.slf4j.Slf4j;

import org.jz.admin.common.Response;
import org.jz.admin.application.FileCmdService;
import org.jz.admin.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileCmdService fileCmdService;

    @PostMapping("list")
    public Response list(@Valid @RequestBody FileListQry query) {
        return fileCmdService.list(query);
    }

    @PostMapping("delete")
    public Response delete(@Valid @RequestBody FileDeleteCmd cmd) {
        return fileCmdService.delete(cmd);
    }

    @PostMapping("makeDir")
    public Response makeDir(@Valid @RequestBody FileMakeDirCmd cmd) {
        return fileCmdService.makeDir(cmd);
    }

    @PostMapping("rename")
    public Response rename(@Valid @RequestBody FileRenameCmd cmd) {
        return fileCmdService.rename(cmd);
    }

    @GetMapping("chunkUpload")
    public Response checkUpProgress(@Valid FileCheckUpProgressCmd cmd) {
        return fileCmdService.checkUpProgress(cmd);
    }

    @PostMapping("chunkUpload")
    public Response chunkUpload(MultipartFile file, @Valid FileChunkUploadCmd cmd) {
        return fileCmdService.chunkUpload(file, cmd);
    }

    @PostMapping("merge")
    public Response merge(@Valid @RequestBody FileMergeCmd cmd) {
        return fileCmdService.merge(cmd);
    }

    @PostMapping("copy")
    public Response copy(@Valid @RequestBody FileMoveOrCopyCmd cmd) {
        return fileCmdService.copy(cmd);
    }

    @PostMapping("move")
    public Response move(@Valid @RequestBody FileMoveOrCopyCmd cmd) {
        return fileCmdService.move(cmd);
    }

    @PostMapping("download")
    public void download(@Valid @RequestBody FileDownloadCmd cmd) {
        fileCmdService.download(cmd);
    }

}

