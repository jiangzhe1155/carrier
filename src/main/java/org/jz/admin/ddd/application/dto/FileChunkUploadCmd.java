package org.jz.admin.ddd.application.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileChunkUploadCmd {

    @NotNull
    private Integer chunkNumber;
    @NotEmpty
    private String identifier;
    private Integer totalChunks;
    MultipartFile file;
}
