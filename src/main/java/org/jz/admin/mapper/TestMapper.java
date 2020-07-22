package org.jz.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.jz.admin.ddd.MyBaseMapper;
import org.jz.admin.entity.TFile;

/**
 * @author jz
 * @date 2020/07/22
 */
@Mapper
public interface TestMapper extends MyBaseMapper<TFile> {

}
