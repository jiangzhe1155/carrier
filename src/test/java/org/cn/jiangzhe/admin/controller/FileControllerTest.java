package org.cn.jiangzhe.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.cn.jiangzhe.admin.entity.FileStatusEnum;
import org.cn.jiangzhe.admin.entity.FileTypeEnum;
import org.cn.jiangzhe.admin.entity.TFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;


@SpringBootTest
public class FileControllerTest {

    private String basePath = "/";

    private String fileName1 = "/Users/macbook/Downloads/模拟面试第一期作业.md";
    private String fileName2 = "C:\\Users\\jiangzhe\\Desktop\\v2.0-JavaGuide2.pdf";

    @Autowired
    FileMapper fileEngineMapper;

    @Test
    public void uploadFile() {
        fileEngineMapper.selectList(null);
    }

    @Test
    public void uploadFiles() {

    }

    @Autowired
    FileMapper fileMapper;

    @Test
    public void getFiles() {
        TFile file = new TFile();
        Date now = new Date();
        file.setUpdateTime(now);
        file.setCreateTime(now);
        file.setFolderName("wahha");
        file.setFolderId(123L);
        file.setStatus(FileStatusEnum.CREATED);
        file.setRelativePath("/dsfsf");
        file.setUniqueFileName("sxxxx");
        file.setOriginalFileName("sxxxx");

        LambdaQueryWrapper<TFile> sxxxx = new QueryWrapper<TFile>().lambda().select(TFile::getId)
                .eq(TFile::getType, FileTypeEnum.DIR).eq(TFile::getOriginalFileName, "sxxxx");
        fileMapper.update(file, sxxxx);
        LambdaQueryWrapper<TFile> eq =
                new QueryWrapper<TFile>().lambda().notExists("select id from t_file " + sxxxx.getCustomSqlSegment());

    }

}
