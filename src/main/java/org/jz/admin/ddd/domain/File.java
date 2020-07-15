package org.jz.admin.ddd.domain;

import lombok.Data;
import org.jz.admin.ddd.domain.valueobject.FileName;
import org.jz.admin.entity.FileTypeEnum;

/**
 * @author 江哲
 * @date 2020/07/15
 */
@Data
public class File {
    private Long id;
    private FileName fileName;
    private String relativePath;
    private Long size;
    private FileTypeEnum typeEnum;


}
