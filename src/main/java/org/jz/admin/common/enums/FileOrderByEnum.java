package org.jz.admin.common.enums;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fasterxml.jackson.annotation.JsonValue;
import org.jz.admin.infrastructure.db.dataobject.FileDO;

/**
 * @author jz
 * @date 2020/06/22
 */
public enum FileOrderByEnum {

    NAME("name", FileDO::getFileName),
    SIZE("size", FileDO::getSize),
    TIME("time", FileDO::getUpdateTime);


    private final SFunction<FileDO, ?> key;

    FileOrderByEnum(String orderBy, SFunction<FileDO, ?> key) {
        this.order = orderBy;
        this.key = key;
    }

    @JsonValue
    private final String order;


    public SFunction<FileDO, ?> getKey() {
        return key;
    }
}
