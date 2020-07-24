package org.jz.admin.infrastructure.db.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jz.admin.common.enums.FileStatusEnum;

import java.util.Date;

/**
 * (TFieStorage)
 *
 * @author jz
 * @since 2020-06-26
 */
@Data
@Accessors(chain = true)
@TableName("t_file_resource")
public class FileResourceDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("`path`")
    private String path;

    @TableField("`identifier`")
    private String identifier;

    @TableField("`status`")
    private FileStatusEnum status;

    @TableField("`create_time`")
    private Date createTime;

    @TableField("`update_time`")
    private Date updateTime;

}