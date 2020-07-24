package org.jz.admin.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileDownloadCmd {
    @NotEmpty
    private List<Integer> fidList;
}
