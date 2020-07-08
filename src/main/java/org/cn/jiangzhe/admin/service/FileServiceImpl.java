package org.cn.jiangzhe.admin.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.cn.jiangzhe.admin.entity.TFile;
import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jz
 * @date 2020/06/11
 */
@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, TFile> {

    @Autowired
    FileUtilService fileUtilService;

}
