package org.jz.admin.ddd.application.dto;

import lombok.Data;
import org.jz.admin.aspect.FileName;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileMakeDirCmd {
    private String relativePath;

    @FileName
    private String fileName;

}
