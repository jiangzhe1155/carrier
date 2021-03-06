package org.jz.admin.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author jz
 * @date 2020/06/22
 */

public enum FileStatusEnum {

    NEW(0, "初始化"),

    CREATING(1, "创建中"),

    CREATED(2, "创建成功"),

    DELETED(3, "删除");

    FileStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    @EnumValue//标记数据库存的值是code
    private final int code;

    private final String desc;
}
