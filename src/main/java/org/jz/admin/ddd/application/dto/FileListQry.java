package org.jz.admin.ddd.application.dto;

import lombok.Data;
import org.jz.admin.entity.FileOrderByEnum;
import org.jz.admin.entity.FileTypeEnum;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileListQry {

    private String relativePath;

    private FileTypeEnum type;

    private FileOrderByEnum order;

    private Boolean asc;

    private Integer page;

    private int pageSize;

}
