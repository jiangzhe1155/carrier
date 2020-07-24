package org.jz.admin.infrastructure.db.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.common.enums.FileStatusEnum;
import org.jz.admin.common.enums.FileTypeEnum;

import java.util.Date;

/**
 * (FileDO)
 *
 * @author jz
 * @since 2020-06-26
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
@TableName("t_file")
public class FileDO {

    //id
    @TableId(type = IdType.AUTO)
    private Long id;

    //原始文件名
    @TableField("`file_name`")
    private String fileName;

    //大小
    @TableField("`size`")
    private Long size;

    //数据类型
    @TableField("`type`")
    private FileTypeEnum type;

    //状态
    @TableField("`status`")
    private FileStatusEnum status;

    //文件属性
    @TableField("`properties`")
    private Integer properties;

    //父文件夹id
    @TableField("`folder_id`")
    private Long folderId;

    //相对路径
    @TableField("`relative_path`")
    private String relativePath;

    //创建时间
    @TableField("`create_time`")
    private Date createTime;

    //更新时间
    @TableField("`update_time`")
    private Date updateTime;

    //更新时间
    @TableField("`storage_id`")
    private Long storageId;

    private transient String path;
}