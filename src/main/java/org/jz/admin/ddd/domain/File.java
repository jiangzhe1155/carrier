package org.jz.admin.ddd.domain;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;

import java.util.Date;

/**
 * @author jz
 * @date 2020/07/16
 */
@Data
@Accessors(chain = true)
public class File {

    private static final long ROOT_FOLDER_ID = 0L;

    private Long id;
    private String fileName;
    private FileTypeEnum type;
    private String relativePath;
    private Long folderId;
    private FileStatusEnum status;
    private Long size;
    private Long resourceId;

    public File setFileName(String fileName) {
        this.fileName = fileName;
        setType(FileTypeEnum.parseType(FileUtil.extName(fileName)));
        return this;
    }

    public void toNewFileName() {
        String ext = FileUtil.extName(fileName);
        String mainName = FileUtil.mainName(fileName);
        String suf = DateUtil.format(new Date(), "yyyyMMdd_HHmmss");
        String newFileName;
        if (StrUtil.isBlank(ext)) {
            newFileName = mainName + suf;
        } else {
            newFileName = mainName + suf + StrUtil.DOT + ext;
        }
        setRelativePath(StrUtil.removeSuffix(relativePath, fileName) + newFileName);
    }

    public File setRelativePath(String relativePath) {
        relativePath = FileUtil.normalize(relativePath);
        if (StrUtil.isEmpty(relativePath)) {
            id = ROOT_FOLDER_ID;
        }
        this.relativePath = relativePath;
        setFileName(StrUtil.subAfter(relativePath, StrUtil.SLASH, true));
        setType(FileTypeEnum.parseType(getFileName()));
        return this;
    }

    private String getParentFolderPath() {
        return StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
    }

    public File getParentFolder() {
        return new File().setType(FileTypeEnum.DIR).setRelativePath(getParentFolderPath());
    }

}
