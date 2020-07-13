package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.jz.admin.entity.FileStatusEnum;
import org.jz.admin.entity.FileTypeEnum;
import org.jz.admin.entity.TFile;
import org.jz.admin.mapper.FileStoreMapper;
import org.jz.admin.mapper.FileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
public class FileControllerTest {

    @Autowired
    FileMapper fileMapper;
    @Autowired
    FileStoreMapper fileStoreMapper;

    @Test
    public void uploadFile() {
        List<TFile> files = fileMapper.selectList(new LambdaQueryWrapper<TFile>().eq(TFile::getId, 108));
        TFile file = new TFile();
        file.setUniqueFileName("sadasd").setType(FileTypeEnum.DIR);
        file.setStatus(FileStatusEnum.CREATED).setRelativePath("asd");
        fileMapper.insert(file);
        System.out.println(files);
    }

    @Test
    public void uploadFiles() {


        List<Long> fidList = Arrays.asList(98L,99L,47L);

        List<TFile> files = fileMapper.selectBatchIds(fidList);
        LambdaQueryWrapper<TFile> wrapper = new LambdaQueryWrapper<>();
        for (TFile file : files) {
            wrapper.or(w -> w.eq(TFile::getRelativePath, file.getRelativePath())
                    .likeRight(file.getType().equals(FileTypeEnum.DIR), TFile::getRelativePath,
                            file.getRelativePath() + StrUtil.SLASH));
        }
        System.out.println(wrapper.getSqlSegment());
        fileMapper.getFileListWithRealPath(wrapper);
    }

    @Test
    public void reset() {
        fileMapper.delete(Wrappers.emptyWrapper());
        fileStoreMapper.delete(Wrappers.emptyWrapper());
    }


}
