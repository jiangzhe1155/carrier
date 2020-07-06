package org.cn.jiangzhe.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.cn.jiangzhe.admin.entity.FileStatusEnum;
import org.cn.jiangzhe.admin.entity.TFieStorage;
import org.cn.jiangzhe.admin.entity.TFile;
import org.cn.jiangzhe.admin.mapper.FieStorageMapper;
import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class FileControllerTest {

    private String basePath = "/";

    private String fileName1 = "/Users/macbook/Downloads/模拟面试第一期作业.md";
    private String fileName2 = "C:\\Users\\jiangzhe\\Desktop\\v2.0-JavaGuide2.pdf";

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FieStorageMapper fieStorageMapper;

    @Test
    public void uploadFile() {

    }

    @Test
    public void uploadFiles() {

    }


}
