package org.jz.admin.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileChunkUploadCmd {

    @NotNull
    @Min(value = 1)
    private Integer chunkNumber;

    @NotBlank
    private String identifier;

    @NotNull
    @Min(value = 1)
    private Integer chunkSize;


}
