package org.jz.admin.ddd.application.dto;

import lombok.Data;
import org.jz.admin.aspect.FileName;

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

    @FileName
    private String filename;

    @Min(value = 1)
    @NotNull
    private Integer totalSize;
}
