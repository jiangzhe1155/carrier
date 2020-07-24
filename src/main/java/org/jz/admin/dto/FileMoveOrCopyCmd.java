package org.jz.admin.dto;

import lombok.Data;
import org.jz.admin.dto.co.FileMoveOrCopyCo;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileMoveOrCopyCmd {
    @NotEmpty
    private List<FileMoveOrCopyCo> fileList;
}
