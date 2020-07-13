package org.jz.admin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.jz.admin.controller.FileController;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author jz
 * @date 2020/06/16
 */
@Service
public class FileUtilService {

    public String absPath(String relativePath, String fileName) {
        return FileUtil.getAbsolutePath(StrUtil.join(File.separator, FileController.DEMO_DIR,
                StrUtil.nullToEmpty(relativePath), StrUtil.nullToEmpty(fileName)));
    }

}
