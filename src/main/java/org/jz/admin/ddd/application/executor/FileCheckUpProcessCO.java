package org.jz.admin.ddd.application.executor;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
public class FileCheckUpProcessCO {

    private Boolean skipUpload;
    private Long id;
    private List<Integer> uploaded;

}
