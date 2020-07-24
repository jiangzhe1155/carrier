package org.jz.admin.dto;

import lombok.Data;
import org.jz.admin.common.enums.FileOrderByEnum;
import org.jz.admin.common.enums.FileTypeEnum;

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
