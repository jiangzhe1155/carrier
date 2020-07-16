package org.jz.admin.ddd.domain;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.controller.FileController;
import org.jz.admin.entity.FileStatusEnum;

import java.io.File;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
public class FileResource {

    public final static String DEMO_DIR = "public/";

    private Long id;
    private FileName fileName;
    private String identifier;
    private Integer totalSize;
    private Integer chunkNumber;
    private Integer chunkSize;
    private Integer currentChunkSize;
//    private String relativePath;
    private Integer totalChunks;
    private FileStatusEnum status;
    private String path;

    public boolean isCreating() {
        return status.equals(FileStatusEnum.CREATING);
    }

    public boolean isCreated() {
        return status.equals(FileStatusEnum.CREATING);
    }

    public FileResource generateRealPath() {
        this.path = StrUtil.join(File.separator, FileController.DEMO_DIR, identifier, fileName);
        return this;
    }
}
