package org.jz.admin.entity;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author jz
 * @date 2020/06/22
 */

public enum FileTypeEnum {

    DIR(0, "文件夹"),
    IMAGE(1, "图片"),
    DOCUMENT(2, "文档"),
    VIDEO(3, "媒体"),
    OTHER(4, "其他");


    private final String desc;

    FileTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    @EnumValue//标记数据库存的值是code
    private final int code;

}
