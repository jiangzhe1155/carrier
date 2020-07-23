package org.jz.admin.ddd.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.aspect.ServiceException;
import org.jz.admin.entity.FileStatusEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
public class FileResource {

    public final static String DEMO_DIR = "public/";
    private static final String DEMO_DIR2 = "public";

    private Long id;
    private String fileName;
    private String identifier;
    private Integer totalSize;
    private Integer chunkNumber;
    private Integer chunkSize;
    private Integer currentChunkSize;
    //    private String relativePath;
    private Integer totalChunks;
    private FileStatusEnum status;
    private String path;

    public boolean isCreated() {
        return status.equals(FileStatusEnum.CREATING);
    }

    public FileResource generateRealPath() {
        this.path = StrUtil.join(File.separator, DEMO_DIR2, identifier);
        return this;
    }

    public void rangeWrite(MultipartFile file) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtil.touch(path), "rw")) {
            randomAccessFile.seek((chunkNumber - 1) * chunkSize);
            randomAccessFile.write(file.getBytes());
        } catch (IOException e) {
            throw new ServiceException("上传文件异常");
        }
    }


    public FileStatusEnum todoUpdateStatus() {
        return chunkNumber.equals(totalChunks) ? FileStatusEnum.CREATED : FileStatusEnum.CREATING;
    }
}
