package org.jz.admin.entity;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    private static final Map<FileTypeEnum, List<String>> FILE_TYPE_ENUM_LIST_MAP = MapUtil.<FileTypeEnum,
            List<String>>builder()
            .put(FileTypeEnum.IMAGE, Arrays.asList("gif", "jpg", "jpeg", "png", "bmp", "webp"))
            .put(FileTypeEnum.DOCUMENT, Arrays.asList("doc", "txt", "docx", "pages", "epub", "pdf", "numbers",
                    "csv", "xls", "xlsx", "keynote", "ppt", "pptx"))
            .put(FileTypeEnum.VIDEO, Arrays.asList("mp4", "m3u8", "rmvb", "avi", "swf", "3gp", "mkv", "flv",
                    "mp3", "wav", "wma", "ogg", "aac", "flac"))
            .build();

    public static FileTypeEnum parseType(String extName) {
        for (Map.Entry<FileTypeEnum, List<String>> entry : FILE_TYPE_ENUM_LIST_MAP.entrySet()) {
            FileTypeEnum fileTypeEnum = entry.getKey();
            List<String> types = entry.getValue();
            if (types.contains(extName)) {
                return fileTypeEnum;
            }
        }
        return FileTypeEnum.OTHER;
    }

}
