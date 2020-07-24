package org.jz.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * (TFieStorage)
 *
 * @author jz
 * @since 2020-06-26
 */
@Data
@Accessors(chain = true)
public class TFileResource {

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