package org.cn.jiangzhe.admin.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.cn.jiangzhe.admin.mapper.FieStorageMapper;
import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;


@SpringBootTest
public class FileControllerTest {

    private String basePath = "/";

    private String fileName1 = "/Users/macbook/Downloads/模拟面试第一期作业.md";
    private String fileName2 = "C:\\Users\\jiangzhe\\Desktop\\v2.0-JavaGuide2.pdf";

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FieStorageMapper fieStorageMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void uploadFile() {
        redisTemplate.opsForValue().setIfAbsent("wahaha", 0L, 10, TimeUnit.SECONDS);
        redisTemplate.opsForValue().increment("wahaha", 1);


    }

    @Test
    public void uploadFiles() {

    }

    @Test
    public void reset() {
        fileMapper.delete(Wrappers.emptyWrapper());
        fieStorageMapper.delete(Wrappers.emptyWrapper());

    }

}
