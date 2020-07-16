package org.jz.admin.ddd.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.entity.FileStatusEnum;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
public class FileResource {

    private Long id;
    private FileName name;
    private String identifier;
    private Integer totalSize;
    private Integer chunkNumber;
    private Integer chunkSize;
    private Integer currentChunkSize;
    private String relativePath;
    private Integer totalChunks;
    private FileStatusEnum status;

    public boolean isCreating() {
        return status.equals(FileStatusEnum.CREATING);
    }

    public boolean isCreated() {
        return status.equals(FileStatusEnum.CREATING);
    }
}
