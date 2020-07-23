package org.jz.admin.ddd.application.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;


/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileRenameCmd {

    private String relativePath;

    @FileName
    private String targetName;
}
