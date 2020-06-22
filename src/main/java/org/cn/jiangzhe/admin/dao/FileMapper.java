package org.cn.jiangzhe.admin.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.cn.jiangzhe.admin.entity.TFile;

/**
 * (TFileEngine)表数据库访问层
 *
 * @author makejava
 * @since 2020-06-13 11:19:26
 */
@Mapper
public interface FileMapper extends BaseMapper<TFile> {


    int insertNotExist(TFile file, @Param(Constants.WRAPPER) LambdaQueryWrapper<Object> wrapper);


}