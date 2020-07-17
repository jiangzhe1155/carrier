package org.jz.admin.ddd.application.dto;

import lombok.Data;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileCheckUpProgressCmd {
    private String identifier;
    private String filename;


}
