package org.cn.jiangzhe.admin.controller;

import org.cn.jiangzhe.admin.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.util.*;


@SpringBootTest
public class FileControllerTest {

    private String basePath = "/";

    private String fileName1 = "/Users/macbook/Downloads/模拟面试第一期作业.md";
    private String fileName2 = "C:\\Users\\jiangzhe\\Desktop\\v2.0-JavaGuide2.pdf";

    @Autowired
    FileMapper fileEngineMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void uploadFile() {
        Set<String> keys = (Set<String>) redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keysTmp = new HashSet<>();
            Cursor<byte[]> cursor =
                    connection.scan(new ScanOptions.ScanOptionsBuilder().match("wa*").build());
            while (cursor.hasNext()) {
                keysTmp.add(new String(cursor.next()));
            }
            return keysTmp;
        });
        System.out.println(keys);
    }

    @Test
    public void uploadFiles() {

    }

    @Autowired
    FileMapper fileMapper;

}
