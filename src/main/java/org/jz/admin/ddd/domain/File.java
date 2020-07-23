package org.jz.admin.ddd.domain;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.entity.FileStatusEnum;

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
    private FileResource resource;

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
        return description.isFolder();
    }

    public void rename(String targetName) {
        setDescription(description.rename(targetName));
    }

    public boolean isSameOrParentDir(File path) {
        return description.isSameOrParentFolder(path.getDescription());
    }

    public boolean isParentDir(File path) {
        return description.isParent(path.getDescription());
    }


    public File newSubFile(String fileName, boolean folder) {
        return new File().setFolderId(id).setDescription(description.subFile(fileName, folder));
    }
}
