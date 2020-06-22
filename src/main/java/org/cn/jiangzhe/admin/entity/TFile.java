package org.cn.jiangzhe.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * (TFile)
 *
 * @author jz
 * @since 2020-06-21
 */
@EqualsAndHashCode(callSuper = false)
@Data
@TableName("t_file")
public class TFile extends Model<TFile> {

    //id
    @TableId(type = IdType.AUTO)
    private Long id;

    //原始文件名
    @TableField("`original_file_name`")
    private String originalFileName;

    //拓展名
    @TableField("`ext`")
    private String ext;

    //数据类型
    @TableField("`type`")
    private Integer type;

    //状态
    @TableField("`status`")
    private FileStatusEnum status;

    //文件属性
    @TableField("`properties`")
    private Integer properties;

    //唯一文件名
    @TableField("`unique_file_name`")
    private String uniqueFileName;

    //父文件夹id
    @TableField("`folder_id`")
    private Integer folderId;

    //父文件夹名称
    @TableField("`folderName`")
    private String folderName;

    //相对路径
    @TableField("`relative_path`")
    private String relativePath;

    //uniqueIdentifier
    @TableField("`uniqueIdentifier`")
    private String md5;

    //渠道来源
    @TableField("`channel`")
    private Integer channel;

    //创建时间
    @TableField("`create_time`")
    private Date createTime;

    //更新时间
    @TableField("`update_time`")
    private Date updateTime;

}