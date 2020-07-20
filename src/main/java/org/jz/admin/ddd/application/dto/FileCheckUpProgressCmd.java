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
public class FileCheckUpProgressCmd {

    @NotBlank
    private String identifier;

    @NotBlank
    private String filename;

    @NotNull
    @Min(1)
    private Integer totalSize;
}
