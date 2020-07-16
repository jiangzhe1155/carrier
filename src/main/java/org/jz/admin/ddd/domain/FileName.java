package org.jz.admin.ddd.domain;

import cn.hutool.core.io.FileUtil;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;
import org.jz.admin.aspect.ServiceException;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
public class FileName {

    private String name;

    public FileName setName(String fileName) {
        if (FileUtil.containsInvalid(fileName)) {
            throw new ServiceException("文件名不合法");
        }
        name = fileName;
        return this;
    }

    public static FileName valueOf(String fileName) {
        return new FileName().setName(fileName);
    }
}
