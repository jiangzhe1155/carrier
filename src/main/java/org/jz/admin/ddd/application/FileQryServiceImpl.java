package org.jz.admin.ddd.application;

import org.jz.admin.common.Response;
import org.jz.admin.ddd.application.dto.FileListQry;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author jz
 * @date 2020/07/14
 */
@Service
public class FileQryServiceImpl {



    public Response list(@RequestBody FileListQry query) {
        return null;
    }

}
