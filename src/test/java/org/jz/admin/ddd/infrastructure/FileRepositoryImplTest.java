package org.jz.admin.ddd.infrastructure;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.jz.admin.entity.TFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;

@SpringBootTest
class FileRepositoryImplTest {

    @Autowired
    FileRepositoryImpl fileRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void getFilePage() throws JsonProcessingException {
//        assert fileRepository.getFileByRelativePath("") == 0L;
        Set<Integer> members = redisTemplate.opsForSet().members("asdasd");
        System.out.println(objectMapper.writeValueAsString(members));

        System.out.println(redisTemplate.opsForSet().isMember("asdasd", 1));
    }

    @Test
    void getFolderIdByRelativePath() throws JsonProcessingException {
        Page<TFile> filePage = fileRepository.getFilePage(0L, null, TFile::getFileName, true, 1, 5);
        String s = objectMapper.writeValueAsString(filePage);
        System.out.println(s);
    }


}