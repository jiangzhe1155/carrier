package org.jz.admin.ddd.infrastructure;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.jz.admin.common.enums.FileStatusEnum;
import org.jz.admin.common.enums.FileTypeEnum;
import org.jz.admin.infrastructure.db.dataobject.FileDO;
import org.jz.admin.infrastructure.db.repository.FileRepositoryImpl;
import org.jz.admin.infrastructure.db.FileMapper;
import org.jz.admin.infrastructure.db.FileStoreMapper;
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
    void getFolderIdByRelativePath() {
        fileRepository.getFileByRelativePath("asdad");
    }


    @Autowired
    FileMapper fileMapper;
    @Autowired
    FileStoreMapper fileStoreMapper;


    @Test
    void reset() {
        fileMapper.delete(new LambdaQueryWrapper<>());
        fileStoreMapper.delete(new LambdaQueryWrapper<>());
    }


    @Test
    void test() {
        Set<String> keys = redisTemplate.keys("5656*");
        System.out.println(keys);
        for (String key : keys) {
            redisTemplate.delete(keys);
            System.out.println(key);
        }

        FileDO fileDO =
                new FileDO().setFileName("asdasd")
                        .setRelativePath("asdasdad")
                        .setFolderId(0L)
                        .setStatus(FileStatusEnum.CREATED)
                        .setStorageId(3L)
                        .setSize(23L)
                        .setType(FileTypeEnum.DIR);

//        LambdaQueryWrapper<FileDO> tFileLambdaQueryWrapper = new LambdaQueryWrapper<FileDO>().notExists("select id " +
//                "from" +
//                " t_file where " +
//                "relative_path = '" + fileDO.getRelativePath() + "'");
//        fileMapper.insertWhereNotExist(fileDO, tFileLambdaQueryWrapper);

//        fileMapper.update(fileDO, new LambdaUpdateWrapper<FileDO>().set(FileDO::getFileName, "zzz"));

//        testMapper.insert(fileDO, tFileLambdaQueryWrapper);
    }


}