package org.jz.admin.ddd;

import org.jz.admin.ddd.domain.Description;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.entity.TFile;

/**
 * @author jz
 * @date 2020/07/20
 */
public class FileConvertor {


    public static TFile serialize(File domainObject) {
        if (domainObject == null) {
            return null;
        }
        return new TFile()
                .setFileName(domainObject.getDescription().getFileName())
                .setFolderId(domainObject.getFolderId())
                .setSize(domainObject.getSize())
                .setType(domainObject.getDescription().getType())
                .setStorageId(domainObject.getResourceId())
                .setRelativePath(domainObject.getDescription().getRelativePath())
                .setStatus(domainObject.getStatus()).setId(domainObject.getId());
    }


    public static File deserialize(TFile dataObject) {
        if (dataObject == null) {
            return null;
        }
        return new File().setDescription(new Description(dataObject.getRelativePath(), dataObject.getFileName(),
                dataObject.getType()))
                .setStatus(dataObject.getStatus())
                .setFolderId(dataObject.getFolderId())
                .setSize(dataObject.getSize())
                .setId(dataObject.getId())
                .setResourceId(dataObject.getStorageId());
    }
}
