package org.jz.admin.ddd.application.dto;

import lombok.Data;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileCheckUpProgressCmd {
    private Integer chunkNumber;
    private Integer chunkSize;
    private Integer currentChunkSize;
    private Integer totalSize;
    private String identifier;
    private String filename;
    private String relativePath;
    private Integer totalChunks;
}
