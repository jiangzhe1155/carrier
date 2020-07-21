package org.jz.admin.ddd.application.dto.co;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
@AllArgsConstructor
public class FileMoveOrCopyCo {
    private String relativePath;
    private String targetPath;
}
