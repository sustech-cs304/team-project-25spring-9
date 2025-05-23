package com.mumu.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.entity.People; // Import People entity
import com.mumu.image.mapper.PeopleMapper; // Import PeopleMapper
import com.mumu.image.service.PeopleService; // Import the service being tested
import com.mumu.utils.AjaxJson;

import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// No MockBean needed
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
// No Mockito imports needed
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PeopleController 的集成测试。
 * 使用 @SpringBootTest 加载完整应用上下文。
 * 使用 @AutoConfigureMockMvc 注入 MockMvc。
 * 测试真实的 Service 和 Mapper 交互。
 * 注意：此测试需要配置测试数据源（例如 H2）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class PeopleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 注入真实的 Mapper/Service 用于数据准备和验证
    @Autowired
    private PeopleMapper peopleMapper;
    @Autowired
    private PeopleService peopleService; // Service under test

    private int testUserId = 233;
    private People testPerson1;
    private People testPerson2;

    @BeforeEach
    void setUp() {
        // 清理可能存在的旧数据 (谨慎!)
        // peopleMapper.delete(null);

        // 准备基础数据
        testPerson1 = new People();
        testPerson1.setName("Alice");
        testPerson1.setUserId(233);
        peopleMapper.insert(testPerson1); // ID auto-generated

        testPerson2 = new People();
        testPerson2.setName("Charlie");
        testPerson2.setUserId(233);
        peopleMapper.insert(testPerson2);

    }

    // @AfterEach
    // void tearDown() {
    //     // Cleanup if not using @Transactional
    // }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试更新/添加人物", tags = "PeopleControllerTest")
    void testUpdatePeople() throws Exception {
        // Arrange
        List<String> namesToAddOrUpdate = Arrays.asList("Alice", "Charlie"); // Update Alice (exists), Add Charlie (new)
        String requestBodyJson = objectMapper.writeValueAsString(namesToAddOrUpdate);


        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/people/update")
                        .param("userId", String.valueOf(testUserId)) // userId as request parameter
                        .contentType(MediaType.APPLICATION_JSON) // Set content type for @RequestBody
                        .content(requestBodyJson)) // Send names list as JSON body
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                ;

    }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试删除人物", tags = "PeopleControllerTest")
    void testDeletePeople() throws Exception {
        // Arrange
        List<String> namesToDelete = Arrays.asList("Alice", "NonExistent"); // Delete Alice (exists), attempt NonExistent
        String requestBodyJson = objectMapper.writeValueAsString(namesToDelete);


        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/people/delete")
                        .param("userId", String.valueOf(testUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                ;

    }

    @Test
    @Transactional // Read-only, but good practice
    @ApiOperation(value = "测试获取人物列表", tags = "PeopleControllerTest")
    void testGetPeopleList() throws Exception {
        // Arrange (Data setup in @BeforeEach)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/people/list") // Changed to POST as per controller
                        .param("userId", String.valueOf(testUserId))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) // Simple param, form encoding is fine
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
                // Expecting Alice and Bob from setUp
    }
}
