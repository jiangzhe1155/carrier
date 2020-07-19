package org.jz.admin.ddd.application.executor;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileCheckUpProgressCO {

    private Boolean skipUpload;
    private Long id;
    private Set<Integer> uploaded;

}
