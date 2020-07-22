package org.jz.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jz.admin.entity.TFileStore;

/**
 * (TFieStorage)表数据库访问层
 *
 * @author makejava
 * @since 2020-06-26 08:31:05
 */
@Mapper
public interface FileStoreMapper extends BaseMapper<TFileStore> {


}