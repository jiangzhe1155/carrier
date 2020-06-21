package org.cn.jiangzhe.admin.controller;

import org.cn.jiangzhe.admin.dao.FileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


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

    @Test
    public void getFiles() {

    }

}
