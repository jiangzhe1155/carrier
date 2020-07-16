package org.jz.admin.ddd.domain;

import lombok.Data;
import org.jz.admin.entity.FileTypeEnum;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
public class File extends AbstractFile {

    private Long id;
    private String name;
    private FileTypeEnum type;

}
