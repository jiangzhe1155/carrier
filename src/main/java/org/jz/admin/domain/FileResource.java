package org.jz.admin.domain;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.common.ServiceException;
import org.jz.admin.common.enums.FileStatusEnum;
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

    private static final String DEMO_DIR = "public";

    private Long id;
    private String fileName;
    private String identifier;
    private Integer totalSize;
    private Integer chunkNumber;
    private Integer chunkSize;
    private Integer currentChunkSize;
    private Integer totalChunks;
    private FileStatusEnum status;
    private String path;

    public boolean isCreated() {
        return status.equals(FileStatusEnum.CREATING);
    }

    public void generateRealPath() {
        this.path = StrUtil.join(File.separator, DEMO_DIR, identifier);
    }

    public void rangeWrite(MultipartFile file) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtil.touch(path), "rw")) {
            randomAccessFile.seek((chunkNumber - 1) * chunkSize);
            randomAccessFile.write(file.getBytes());
        } catch (IOException e) {
            throw new ServiceException("上传文件异常");
        }
    }

    public FileStatusEnum readyToUpdateStatus() {
        return chunkNumber.equals(totalChunks) ? FileStatusEnum.CREATED : FileStatusEnum.CREATING;
    }
}
