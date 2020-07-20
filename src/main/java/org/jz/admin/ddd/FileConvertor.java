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
                .setId(domainObject.getId())
                .setFileName(domainObject.getDescription().getRelativePath())
                .setFolderId(domainObject.getFolderId())
                .setSize(domainObject.getSize())
                .setType(domainObject.getDescription().getType())
                .setStorageId(domainObject.getResourceId())
                .setRelativePath(domainObject.getDescription().getRelativePath())
                .setStatus(domainObject.getStatus());
    }


    public static File deserialize(TFile dataObject) {
        if (dataObject == null) {
            return null;
        }
        return new File().setId(dataObject.getId())
                .setStatus(dataObject.getStatus())
                .setFolderId(dataObject.getFolderId())
                .setSize(dataObject.getSize())
                .setDescription(new Description(dataObject.getRelativePath(), dataObject.getFileName(),
                        dataObject.getType()));
    }
}
