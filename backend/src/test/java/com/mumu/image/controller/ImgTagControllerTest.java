package com.mumu.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.People;
import com.mumu.image.entity.Tag; // Import Tag entity
import com.mumu.image.mapper.ImgMapper;
import com.mumu.image.mapper.ImgTagMapper; // Import ImgTagMapper
import com.mumu.image.mapper.TagMapper; // Import TagMapper
import com.mumu.image.service.ImgTagService; // Import the service being tested
import com.mumu.utils.AjaxJson;

import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// No MockBean needed as we test the real service interaction
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
// No Mockito imports needed
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ImgTagController 的集成测试。
 * 使用 @SpringBootTest 加载完整应用上下文。
 * 使用 @AutoConfigureMockMvc 注入 MockMvc。
 * 测试真实的 Service 和 Mapper 交互。
 * 注意：此测试需要配置测试数据源（例如 H2）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ImgTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 注入真实的 Mapper/Service 用于数据准备和验证
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private TagMapper tagMapper; // Changed from PeopleMapper
    @Autowired
    private ImgTagMapper imgTagMapper; // Changed from ImgPeopleMapper
    @Autowired
    private ImgTagService imgTagService; // Service under test

    private int testUserId = 1;
    private Img testImg;
    private Tag testTag1; // Changed from People
    private Tag testTag2; // For adding test

    @BeforeEach
    void setUp() {
        // 清理可能存在的旧数据 (谨慎!)
        // imgTagMapper.delete(null);
        // imgMapper.delete(null);
        // tagMapper.delete(null);

        // 准备基础数据
        testTag1 = new Tag();
        testTag1.setTagName("testTag1");
        testTag1.setUserId(1);
        tagMapper.insert(testTag1); // ID auto-generated
        testTag2 = new Tag();
        testTag2.setTagName("testTag2");
        testTag2.setUserId(2);
        tagMapper.insert(testTag2); // ID auto-generated



        testImg = new Img();
        testImg.setUserId(testUserId);
        testImg.setImgName("Tag Test Img");
        testImg.setImgDate(new Date());
        testImg.setValid(true);
        imgMapper.insert(testImg); // ID auto-generated

        // Link Img with Tag1 initially
        imgTagService.addImgTag(testUserId, testImg.getImgId(), testTag1.getTagName()); // Use service method
    }

    // @AfterEach
    // void tearDown() {
    //     // Cleanup if not using @Transactional
    // }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试删除图片标签关联", tags = "ImgTagControllerTest")
    void testDeleteImgTag() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String tagNameToDelete = testTag1.getTagName(); // Tag linked in setUp

        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(imgId));
        params.add("userId", String.valueOf(testUserId));
        params.add("tag", tagNameToDelete); // Changed parameter name

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgtag/delete") // Changed path
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // Assuming the service method returns boolean true on success
                .andExpect(jsonPath("$.data").value(true));

    }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试添加图片标签关联", tags = "ImgTagControllerTest")
    void testAddImgTag() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String tagNameToAdd = testTag2.getTagName(); // Tag NOT linked in setUp



        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(11));
        params.add("userId", String.valueOf(4));
        params.add("tag", "tagNameToAdd"); // Changed parameter name

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgtag/add") // Changed path
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

    }

    @Test
    @Transactional // Modifies DB - tests adding a tag that doesn't exist yet
    @ApiOperation(value = "测试添加图片标签关联 (标签不存在)", tags = "ImgTagControllerTest")
    void testAddImgTag_NewTag() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String newTagName = "Travel"; // Tag not in DB yet


        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(11));
        params.add("userId", String.valueOf(4));
        params.add("tag", newTagName); // Changed parameter name

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgtag/add") // Changed path
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

    }
}
