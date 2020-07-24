package org.jz.admin.dto;

import lombok.Data;
import org.jz.admin.common.FileName;

import javax.validation.constraints.NotBlank;


/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileRenameCmd {

    private String relativePath;

    @FileName
    @NotBlank
    private String targetName;
}
