package com.mumu.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.People;
import com.mumu.image.mapper.ImgMapper;
import com.mumu.image.mapper.ImgPeopleMapper;
import com.mumu.image.mapper.PeopleMapper;
import com.mumu.image.service.ImgPeopleService; // Import the service being tested
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
 * ImgPeopleController 的集成测试。
 * 使用 @SpringBootTest 加载完整应用上下文。
 * 使用 @AutoConfigureMockMvc 注入 MockMvc。
 * 测试真实的 Service 和 Mapper 交互。
 * 注意：此测试需要配置测试数据源（例如 H2）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ImgPeopleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 注入真实的 Mapper/Service 用于数据准备和验证
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private PeopleMapper peopleMapper;
    @Autowired
    private ImgPeopleMapper imgPeopleMapper; // For verification
    @Autowired
    private ImgPeopleService imgPeopleService; // Service under test

    private int testUserId = 1;
    private Img testImg;
    private People testPerson1;
    private People testPerson2; // For adding test

    @BeforeEach
    void setUp() {
        // 清理可能存在的旧数据 (谨慎!)
        // imgPeopleMapper.delete(null);
        // imgMapper.delete(null);
        // peopleMapper.delete(null);

        // 准备基础数据
        testPerson1 = new People();
        testPerson1.setName("testTag1");
        testPerson1.setUserId(1);
        peopleMapper.insert(testPerson1); // ID auto-generated

        testPerson2 = new People();
        testPerson2.setName("testTag2");
        testPerson2.setUserId(1);
        peopleMapper.insert(testPerson2);

        testImg = new Img();
        testImg.setUserId(testUserId);
        testImg.setImgName("Person Test Img");
        testImg.setImgDate(new Date());
        testImg.setValid(true);
        imgMapper.insert(testImg); // ID auto-generated

        // Link Img with Person1 initially
        // Assuming ImgPeopleService handles checking existence or uses a direct mapper call
        // For setup, let's use the service method if it handles creation cleanly,
        // otherwise use the mapper directly. Let's assume service handles it.
        imgPeopleService.addImgPeople(testUserId, testImg.getImgId(), testPerson1.getName());
    }

    // @AfterEach
    // void tearDown() {
    //     // Cleanup if not using @Transactional
    // }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试删除图片人物关联", tags = "ImgPeopleControllerTest")
    void testDeleteImgPeople() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String personNameToDelete = testPerson1.getName(); // Person linked in setUp

        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(imgId));
        params.add("userId", String.valueOf(testUserId));
        params.add("people", personNameToDelete);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgpeople/delete")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) // Use form encoding for simple params
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // Assuming the service method returns boolean true on success
                .andExpect(jsonPath("$.data").value(true));

    }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试添加图片人物关联", tags = "ImgPeopleControllerTest")
    void testAddImgPeople() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String personNameToAdd = testPerson2.getName(); // Person NOT linked in setUp


        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(imgId));
        params.add("userId", String.valueOf(testUserId));
        params.add("people", personNameToAdd);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgpeople/add")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // Assuming the service method returns boolean true on success
                .andExpect(jsonPath("$.data").value(true));

    }

    @Test
    @Transactional // Modifies DB - tests adding a person that doesn't exist yet
    @ApiOperation(value = "测试添加图片人物关联 (人物不存在)", tags = "ImgPeopleControllerTest")
    void testAddImgPeople_NewPerson() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String newPersonName = "Charlie"; // Person not in DB yet

        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(11));
        params.add("userId", String.valueOf(4));
        params.add("people", "mumu2024");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgpeople/add")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

    }

    @Test
    @Transactional // Doesn't modify DB if person doesn't exist
    @ApiOperation(value = "测试删除图片人物关联 (人物未关联)", tags = "ImgPeopleControllerTest")
    void testDeleteImgPeople_NotLinked() throws Exception {
        // Arrange
        int imgId = testImg.getImgId();
        String personNotLinked = testPerson2.getName(); // Person exists but not linked in setUp

        // Create request parameters
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("imgId", String.valueOf(imgId));
        params.add("userId", String.valueOf(testUserId));
        params.add("people", personNotLinked);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/imgpeople/delete")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // Assuming the service method returns false if no relationship was found/deleted
                .andExpect(jsonPath("$.data").value(false));

    }
}
