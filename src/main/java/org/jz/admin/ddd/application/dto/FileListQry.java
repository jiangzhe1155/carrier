package org.jz.admin.ddd.application.dto;

import lombok.Data;
import org.jz.admin.entity.FileTypeEnum;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileListQry {

    private String relativePath;

    private FileTypeEnum fileType;

    private Boolean asc;

    private Integer page;

    private Integer pageSize;

}
