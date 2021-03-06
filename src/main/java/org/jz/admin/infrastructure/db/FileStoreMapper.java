package org.jz.admin.infrastructure.db;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jz.admin.infrastructure.db.dataobject.FileResourceDO;

/**
 * (TFieStorage)表数据库访问层
 *
 * @author makejava
 * @since 2020-06-26 08:31:05
 */
@Mapper
public interface FileStoreMapper extends BaseMapper<FileResourceDO> {


}