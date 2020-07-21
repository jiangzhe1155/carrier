package org.cn.jiangzhe.admin.controller;

import cn.hutool.core.io.FileUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest
public class QiNiuYunFileTest {

    private String fileName1 = "C:\\Users\\jiangzhe\\Desktop\\ops_data.csv";
    private String fileName2 = "C:\\Users\\jiangzhe\\Desktop\\v2.0-JavaGuide2.pdf";

    private MockMvc mockMvc;

    @BeforeEach
    void setup(WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void uploadFile() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .multipart("/uploadFiles")
                .file(new MockMultipartFile("multipartFiles", "ops_data.csv", null, FileUtil.readBytes(fileName1)))
                .file(new MockMultipartFile("multipartFiles", "v2.0-JavaGuide2.pdf", null,
                        FileUtil.readBytes(fileName2)))
                .contentType(MediaType.MULTIPART_FORM_DATA).accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void uploadFiles() {

    }

    @Test
    public void getFiles() {

    }
}