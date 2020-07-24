package org.jz.admin.infrastructure.db.convertor;

import org.jz.admin.domain.valueobject.Description;
import org.jz.admin.domain.File;
import org.jz.admin.infrastructure.db.dataobject.FileDO;

/**
 * @author jz
 * @date 2020/07/20
 */
public class FileConvertor {


    public static FileDO serialize(File domainObject) {
        if (domainObject == null) {
            return null;
        }

        return new FileDO()
                .setFileName(domainObject.getDescription().getFileName())
                .setFolderId(domainObject.getFolderId())
                .setSize(domainObject.getSize())
                .setType(domainObject.getDescription().getType())
                .setStorageId(domainObject.getResourceId())
                .setRelativePath(domainObject.getRelativePath())
                .setStatus(domainObject.getStatus()).setId(domainObject.getId());
    }


    public static File deserialize(FileDO dataObject) {
        if (dataObject == null) {
            return null;
        }
        return new File().setDescription(new Description(dataObject.getRelativePath(), dataObject.getFileName(),
                dataObject.getType()))
                .setStatus(dataObject.getStatus())
                .setFolderId(dataObject.getFolderId())
                .setSize(dataObject.getSize())
                .setId(dataObject.getId())
                .setResourceId(dataObject.getStorageId())
                .setPath(dataObject.getPath());
    }
}
