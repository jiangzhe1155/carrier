package org.jz.admin.ddd.application.dto;

import lombok.Data;
import org.jz.admin.ddd.application.dto.co.FileMoveOrCopyCo;

import java.util.List;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileMoveOrCopyCmd {
    private List<FileMoveOrCopyCo> fileList;
}
