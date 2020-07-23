package org.jz.admin.ddd.application.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileMergeCmd {

    @NotNull
    private Long storageId;
    @NotBlank
    private String relativePath;
    @Min(1)
    @NotNull
    private Long totalSize;

}
