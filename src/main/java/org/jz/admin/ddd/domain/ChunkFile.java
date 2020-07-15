package org.jz.admin.ddd.domain;

import lombok.Data;
import org.jz.admin.ddd.domain.valueobject.FileName;

/**
 * @author 江哲
 * @date 2020/07/15
 */
@Data
public class ChunkFile {

    private Integer chunkNumber;
    private Integer chunkSize;
    private Integer currentChunkSize;
    private Integer totalSize;
    private String identifier;
    private FileName fileName;
    private String relativePath;
    private Integer totalChunks;

}
