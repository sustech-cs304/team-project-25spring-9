package com.mumu.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.entity.Tag; // Import Tag entity
import com.mumu.image.mapper.TagMapper; // Import TagMapper
import com.mumu.image.service.TagService; // Import the service being tested
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
 * TagController 的集成测试。
 * 使用 @SpringBootTest 加载完整应用上下文。
 * 使用 @AutoConfigureMockMvc 注入 MockMvc。
 * 测试真实的 Service 和 Mapper 交互。
 * 注意：此测试需要配置测试数据源（例如 H2）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 注入真实的 Mapper/Service 用于数据准备和验证
    @Autowired
    private TagMapper tagMapper; // Changed from PeopleMapper
    @Autowired
    private TagService tagService; // Service under test

    private int testUserId = 235;
    private Tag testTag1; // Changed from People
    private Tag testTag2; // Changed from People

    @BeforeEach
    void setUp() {
        // 清理可能存在的旧数据 (谨慎!)
        // tagMapper.delete(null);
        testTag1 = new Tag();
        testTag1.setTagName("Nature");
        testTag1.setUserId(235);
        tagMapper.insert(testTag1); // ID auto-generated
        testTag2 = new Tag();
        testTag2.setTagName("Urban");
        testTag2.setUserId(235);
        tagMapper.insert(testTag2); // ID auto-generated
    }

    // @AfterEach
    // void tearDown() {
    //     // Cleanup if not using @Transactional
    // }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试更新/添加标签", tags = "TagControllerTest")
    void testUpdateTag() throws Exception {
        // Arrange
        List<String> namesToAddOrUpdate = Arrays.asList("Nature", "Urban"); // Update Nature (exists), Add Travel (new)
        String requestBodyJson = objectMapper.writeValueAsString(namesToAddOrUpdate);


        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/tag/updatetest") // Changed path
                        .param("userId", String.valueOf(255)) // userId as request parameter
                        .contentType(MediaType.APPLICATION_JSON) // Set content type for @RequestBody
                        .content(requestBodyJson)) // Send names list as JSON body
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // Assuming the service returns boolean true on success
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试删除标签", tags = "TagControllerTest")
    void testDeleteTag() throws Exception {
        // Arrange
        List<String> namesToDelete = Arrays.asList("Nature", "NonExistent"); // Delete Nature (exists), attempt NonExistent
        String requestBodyJson = objectMapper.writeValueAsString(namesToDelete);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/tag/delete") // Changed path
                        .param("userId", String.valueOf(testUserId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBodyJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

    }

    @Test
    @Transactional // Read-only, but good practice
    @ApiOperation(value = "测试获取标签列表", tags = "TagControllerTest")
    void testGetTagList() throws Exception {
        // Arrange (Data setup in @BeforeEach)

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/tag/list") // Changed path, still POST
                        .param("userId", String.valueOf(testUserId))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)) // Simple param
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                // Expecting Nature and Urban from setUp
                // Check if the names are present (order might not be guaranteed)
                .andExpect(jsonPath("$.data[*].tagName", containsInAnyOrder("Nature", "Urban"))) // Changed field name
                .andExpect(jsonPath("$.data[*].tagId", containsInAnyOrder(testTag1.getTagId(), testTag2.getTagId()))); // Changed field name
    }
}
