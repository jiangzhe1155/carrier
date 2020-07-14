package org.jz.admin.ddd.application.dto;

import lombok.Data;

import java.util.List;

/**
 * @author jz
 * @date 2020/07/14
 */
@Data
public class FileDeleteCmd {

    private List<String> relativePaths;
}
