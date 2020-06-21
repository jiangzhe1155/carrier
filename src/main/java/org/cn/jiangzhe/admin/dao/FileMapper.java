package org.cn.jiangzhe.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.cn.jiangzhe.admin.entity.TFile;

/**
 * (TFileEngine)表数据库访问层
 *
 * @author makejava
 * @since 2020-06-13 11:19:26
 */
@Mapper
public interface FileMapper extends BaseMapper<TFile> {

}