package org.jz.admin.ddd.controller;

import lombok.extern.slf4j.Slf4j;
import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.FileServiceI;
import org.jz.admin.ddd.application.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RequestMapping("/file")
@RestController
public class FileController2 {

    @Autowired
    FileServiceI fileService;

    @PostMapping("list")
    public Response list(@Valid @RequestBody FileListQry query) {
        return fileService.list(query);
    }

    @PostMapping("delete")
    public Response delete(@RequestBody FileDeleteCmd cmd) {
        return fileService.delete(cmd);
    }

    @PostMapping("/makeDir")
    public Response makeDir(@RequestBody FileMakeDirCmd cmd) {
        return fileService.makeDir(cmd);
    }

    @PostMapping("rename")
    public Response rename(@RequestBody FileRenameCmd cmd) {
        return fileService.rename(cmd);
    }

    @GetMapping("chunkUpload")
    public Response checkUpProgress(@Valid FileCheckUpProgressCmd cmd) {
        return fileService.checkUpProgress(cmd);
    }

    @PostMapping("chunkUpload")
    public Response chunkUpload(MultipartFile file, @Valid FileChunkUploadCmd cmd) {
        return fileService.chunkUpload(file, cmd);
    }

    @PostMapping("merge")
    public Response merge(@RequestBody FileMergeCmd cmd) {
        return fileService.merge(cmd);
    }

    @PostMapping("copy")
    public Response copy(@RequestBody FileMoveOrCopyCmd cmd) {
        return fileService.copy(cmd);
    }

    @PostMapping("move")
    public Response move(@RequestBody FileMoveOrCopyCmd cmd) {
        return fileService.move(cmd);
    }

    @PostMapping("download")
    public Response download(@RequestBody FileDownloadCmd cmd) {
        return fileService.download(cmd);
    }

}
