package org.jz.admin.ddd.application.executor;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileDeleteCmd;
import org.springframework.stereotype.Component;

/**
 * @author 江哲
 * @date 2020/07/14
 */
@Component
public class FileDeleteCmdExe {
    public Response execute(FileDeleteCmd cmd) {
        return null;
    }
}
