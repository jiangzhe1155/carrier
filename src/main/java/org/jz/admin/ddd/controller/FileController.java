package org.jz.admin.ddd.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jz.admin.aspect.CommonLog;
import org.jz.admin.aspect.ServiceException;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.entity.TFileStore;
import org.jz.admin.mapper.FileMapper;
import org.jz.admin.mapper.FileStoreMapper;
import org.jz.admin.service.FileServiceImpl;
import org.jz.admin.service.FileUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 作为一个文件大致有三种状态：生成、完成、删除
 *
 * @author jz
 * @date 2020/05/14
 */
@Slf4j
@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("list")
    public void list(@RequestBody FileListCmd cmd) {

    }

    @PostMapping("delete")
    public void delete(@RequestBody FileDeleteCmd cmd) {

    }

    @PostMapping("makeDir")
    public void makeDir(@RequestBody FileDeleteCmd cmd) {

    }

    @PostMapping("rename")
    public void rename(@RequestBody FileDeleteCmd cmd) {

    }

    @GetMapping("chunkUpload")
    public void chunkFile(FileChunkUploadFileCmd cmd) {

    }

    @PostMapping("chunkUpload")
    public void chunkUploadFile(@RequestBody MultipartFile file, FileChunkUploadFileCmd cmd) {

    }

    @PostMapping("merge")
    public void merge(@RequestBody FileMergeCmd cmd) {

    }


    @PostMapping("copy")
    public void copy(@RequestBody FileCopyCmd cmd) {

    }

    @PostMapping("move")
    public void move(@RequestBody FileMoveCmd cmd) {

    }

    @PostMapping("download")
    public void download(@RequestBody FileDownCmd cmd) {

    }
}
