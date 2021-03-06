
package org.jz.admin.infrastructure.db;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jz.admin.infrastructure.db.dataobject.FileDO;

import java.util.List;

/**
 * (TFileEngine)表数据库访问层
 *
 * @author makejava
 * @since 2020-06-13 11:19:26
 */
@Mapper
public interface FileMapper extends BaseMapper<FileDO> {

    List<FileDO> getFileListWithSourcePath(@Param(Constants.WRAPPER) Wrapper<FileDO> wrapper);

}