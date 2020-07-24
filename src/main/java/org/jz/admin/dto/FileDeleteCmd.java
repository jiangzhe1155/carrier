package org.jz.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileDeleteCmd {

    @NotEmpty
    private List<String> relativePaths;
}
