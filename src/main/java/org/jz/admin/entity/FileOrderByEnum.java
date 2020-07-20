package org.jz.admin.entity;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author jz
 * @date 2020/06/22
 */
public enum FileOrderByEnum {

    NAME("name", TFile::getFileName),
    SIZE("size", TFile::getSize),
    TIME("time", TFile::getUpdateTime);


    private final SFunction<TFile, ?> key;

    FileOrderByEnum(String orderBy, SFunction<TFile, ?> key) {
        this.order = orderBy;
        this.key = key;
    }

    @JsonValue
    private final String order;


    public SFunction<TFile, ?> getKey(){
        return key;
    }
}
