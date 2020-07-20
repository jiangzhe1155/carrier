package org.jz.admin.ddd;

import org.jz.admin.ddd.domain.File;
import org.jz.admin.entity.TFile;

/**
 * @author jz
 * @date 2020/07/20
 */
public class FileConvertor extends Convertor<File, TFile> {


    @Override
    public TFile serialize(File domainObject) {
        return null;
    }

    @Override
    public File deserialize(TFile domainObject) {
        return null;
    }
}
