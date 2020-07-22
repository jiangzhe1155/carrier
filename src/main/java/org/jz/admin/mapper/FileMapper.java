
package org.jz.admin.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jz.admin.ddd.domain.File;
import org.jz.admin.entity.TFile;

import java.util.List;

/**
 * (TFileEngine)表数据库访问层
 *
 * @author makejava
 * @since 2020-06-13 11:19:26
 */
@Mapper
public interface FileMapper extends BaseMapper<TFile> {

    List<TFile> getFileListWithRealPath(@Param(Constants.WRAPPER) Wrapper<TFile> wrapper);

    int insertWhereNotExist(@Param(Constants.ENTITY) TFile rootDir, @Param(Constants.WRAPPER) Wrapper<TFile> eq);
}