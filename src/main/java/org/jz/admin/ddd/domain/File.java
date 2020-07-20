package org.jz.admin.ddd.domain;

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
    private Description description;
    private Long folderId;
    private FileStatusEnum status;
    private Long size;
    private Long resourceId;

    public File setDescription(Description description) {
        this.description = description;
        if (StrUtil.isBlank(description.getRelativePath())) {
            this.id = ROOT_FOLDER_ID;
        }
        return this;
    }

    public void toNewFileName() {
        setDescription(description.newFileName());
    }

    private String getParentFolderPath() {
        return description.getParentFolderPath();
    }

    public File newParentFolder() {
        return new File().setDescription(new Description(getParentFolderPath(), true));
    }

    public boolean isFolder() {
        return FileTypeEnum.isFolder(description.getType());
    }
}
