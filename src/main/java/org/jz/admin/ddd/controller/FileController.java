package org.jz.admin.ddd.controller;

import lombok.extern.slf4j.Slf4j;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.FileCmdServiceImpl;
import org.jz.admin.ddd.application.FileQryServiceImpl;
import org.jz.admin.ddd.application.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 作为一个文件大致有三种状态：生成、完成、删除
 *
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RequestMapping("/file")
public class FileController {

    @Autowired
    FileCmdServiceImpl fileCmdService;

    @Autowired
    FileQryServiceImpl fileQryService;

    @PostMapping("list")
    public Response list(@RequestBody FileListQry query) {
        return fileQryService.list(query);
    }

    @PostMapping("delete")
    public Response delete(@RequestBody FileDeleteCmd cmd) {
        return fileCmdService.delete(cmd);
    }

    @PostMapping("makeDir")
    public Response makeDir(@RequestBody FileMakeDirCmd cmd) {
        return fileCmdService.makeDir(cmd);
    }

    @PostMapping("rename")
    public Response rename(@RequestBody FileRenameCmd cmd) {
        return fileCmdService.rename(cmd);
    }

    @GetMapping("chunkUpload")
    public Response checkUpProcess(FileCheckUpProcessCmd cmd) {
        return fileCmdService.checkUpProcess(cmd);
    }

    @PostMapping("chunkUpload")
    public Response chunkUpload(FileChunkUploadCmd cmd) {
        return fileCmdService.chunkUpload(cmd);
    }

    @PostMapping("merge")
    public Response merge(@RequestBody FileMergeCmd cmd) {
        return fileCmdService.merge(cmd);
    }

    @PostMapping("copy")
    public Response copy(@RequestBody FileCopyCmd cmd) {
        return fileCmdService.copy(cmd);
    }

    @PostMapping("move")
    public Response move(@RequestBody FileMoveCmd cmd) {
        return fileCmdService.move(cmd);
    }

    @PostMapping("download")
    public Response download(@RequestBody FileDownloadCmd cmd) {
        return fileCmdService.download(cmd);
    }

}

