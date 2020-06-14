package org.cn.jiangzhe.admin.entity;

import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * (TFileEngine)实体类
 *
 * @author makejava
 * @since 2020-06-13 11:19:22
 */
@Data
public class TFileEngine implements Serializable {
    private static final long serialVersionUID = 208525721822434483L;
    /**
    * id
    */
    private Integer id;
    /**
    *  名称
    */
    private String name;
    /**
    * 是否是文件夹
    */
    private Boolean isDir;
    /**
    * 指向父类的文件夹
    */
    private Integer pId;
    /**
    * 状态
    */
    private Integer status;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 更新时间
    */
    private Date updateTime;
    /**
    * 前缀路径
    */
    private String sufPath;
    /**
    * md5
    */
    private String hash;
    /**
    * 渠道来源
    */
    private Integer channel;
}