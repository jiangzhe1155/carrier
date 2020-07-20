package org.jz.admin.ddd.domain;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.jz.admin.entity.FileTypeEnum;

import java.util.Date;

/**
 * @author jz
 * @date 2020/07/20
 */
@Getter
@AllArgsConstructor
public class Description {
    private String fileName;
    private FileTypeEnum type;
    private String relativePath;

    public Description(String relativePath) {
        new Description(relativePath, false);
    }

    public Description(String relativePath, boolean isFolder) {
        this.relativePath = FileUtil.normalize(relativePath);
        this.fileName = StrUtil.subAfter(this.relativePath, StrUtil.SLASH, true);
        if (isFolder || StrUtil.isBlank(relativePath)) {
            this.type = FileTypeEnum.DIR;
        } else {
            this.type = FileTypeEnum.parseType(FileUtil.extName(fileName));
        }
    }

    public Description newFileName() {
        String ext = FileUtil.extName(fileName);
        String mainName = FileUtil.mainName(fileName);
        String suf = DateUtil.format(new Date(), "yyyyMMdd_HHmmss");
        String newFileName;
        if (StrUtil.isBlank(ext)) {
            newFileName = mainName + suf;
        } else {
            newFileName = mainName + suf + StrUtil.DOT + ext;
        }
        String relativePath = StrUtil.removeSuffix(this.relativePath, fileName) + newFileName;
        return new Description(fileName, this.type, relativePath);
    }

    public String getParentFolderPath() {
        return StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
    }
}
