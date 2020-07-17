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

    public String getExt() {
        return FileUtil.extName(fileName);
    }

    public String getMainName() {
        return FileUtil.mainName(fileName);
    }

    public void toNewFileName() {
        String ext = getExt();
        String mainName = getMainName();
        String suf = DateUtil.format(new Date(), "yyyyMMdd_HHmmss");
        String newFileName;
        if (StrUtil.isBlank(ext)) {
            newFileName = mainName + suf;
        } else {
            newFileName = mainName + suf + StrUtil.DOT + ext;
        }
        setRelativePath(StrUtil.removeSuffix(relativePath, fileName) + newFileName);
        setFileName(newFileName);
    }

    public File setRelativePath(String relativePath) {
        FileUtil.normalize(relativePath);
        this.relativePath = relativePath;
        return this;
    }


    public String getParentFolderPath() {
        return StrUtil.subBefore(relativePath, CharUtil.SLASH, true);
    }

    public File getParentFolder() {
        File parentFolder = new File().setType(FileTypeEnum.DIR).setRelativePath(getParentFolderPath());
        if (StrUtil.isEmpty(parentFolder.getRelativePath())) {
            parentFolder.setId(ROOT_FOLDER_ID);
            return parentFolder;
        }

        String parentFolderName = FileUtil.getName(parentFolder.getRelativePath());
        parentFolder.setFileName(parentFolderName);
        return parentFolder;
    }
}
