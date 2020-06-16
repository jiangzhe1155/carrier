package org.cn.jiangzhe.admin.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.cn.jiangzhe.admin.controller.FileController;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author jz
 * @date 2020/06/16
 */
@Service
public class FileUtilService {



    public String fileType(String fileName) {
        return StrUtil.subAfter(fileName, '.', true);
    }

    public String absPath(String relativePath, String fileName) {
        return FileUtil.getAbsolutePath(StrUtil.join(File.separator, FileController.DEMO_DIR,
                StrUtil.nullToEmpty(relativePath), StrUtil.nullToEmpty(fileName)));
    }


}
