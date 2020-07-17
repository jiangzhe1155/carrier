package org.jz.admin.ddd.application.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileCheckUpProgressCmd {

    @NotEmpty
    private String identifier;

    @NotEmpty
    private String filename;
}
