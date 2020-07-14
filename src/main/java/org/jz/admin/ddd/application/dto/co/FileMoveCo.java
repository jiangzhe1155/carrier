package org.jz.admin.ddd.application.dto.co;

import lombok.Data;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileMoveCo {
    private String relativePath;
    private String targetPath;
}
