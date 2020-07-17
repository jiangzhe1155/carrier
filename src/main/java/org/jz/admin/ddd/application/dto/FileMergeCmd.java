package org.jz.admin.ddd.application.dto;

import lombok.Data;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileMergeCmd {

    private String filename;
    private Long storageId;
    private String relativePath;
    private Long totalSize;

}
