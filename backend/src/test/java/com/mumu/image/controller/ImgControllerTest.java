package com.mumu.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.DTO.ImageInfo; // Assuming ImageInfo exists and is deserializable
import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.People;
import com.mumu.image.entity.Tag;
import com.mumu.image.mapper.*; // Import necessary mappers
import com.mumu.image.service.*; // Import necessary services if needed for setup/verification
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;

import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito; // Import Mockito for argument captors if needed
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile; // For creating mock files
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*; // For JUnit assertions like assertNotNull, assertEquals
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ImgController 的集成测试。
 * 使用 @SpringBootTest 加载完整应用上下文。
 * 使用 @AutoConfigureMockMvc 注入 MockMvc。
 * Mock 外部依赖 RestTemplate 和 MinioUtils。
 * 注意：此测试需要配置测试数据源（例如 H2）。
 */
@SpringBootTest
@AutoConfigureMockMvc
class ImgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock 外部服务
    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private MinioUtils minioUtilS;

    // 注入真实的 Mapper/Service 用于数据准备和验证
    @Autowired
    private ImgMapper imgMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private PeopleMapper peopleMapper;
    @Autowired
    private ImgTagMapper imgTagMapper;
    @Autowired
    private ImgPeopleMapper imgPeopleMapper;
    @Autowired
    private ImgService imgService; // Might be needed for complex queries/logic verification

    @Value("${minio.uploadImage}")
    private String ImgPath; // Inject value from properties

    private int testUserId = 1;
    private Img testImg1;
    private Tag testTag1;
    private People testPeople1;

    @BeforeEach
    void setUp() {
        // 清理可能存在的旧数据 (谨慎!)
        // imgTagMapper.delete(null);
        // imgPeopleMapper.delete(null);
        // imgMapper.delete(null);
        // tagMapper.delete(null);
        // peopleMapper.delete(null);

        // 准备基础数据
        testTag1 = new Tag();
        testTag1.setTagName("testTag1");
        testTag1.setUserId(1);
        tagMapper.insert(testTag1); // ID should be auto-generated

        testPeople1 = new People();
        testPeople1.setName("testTag1");
        testPeople1.setUserId(1);
        peopleMapper.insert(testPeople1); // ID should be auto-generated

        testImg1 = new Img();
        testImg1.setUserId(testUserId);
        testImg1.setImgName("Test Image 1");
        testImg1.setImgDate(new Date());
        testImg1.setImgPos("Test Location");
        testImg1.setImgDescribtion("A test description");
        testImg1.setPub(true);
        testImg1.setValid(true);
        // testImg1.setAlbumId(someAlbumId); // If needed
        imgMapper.insert(testImg1); // ID should be auto-generated

    }

    // @AfterEach
    // void tearDown() {
    //     // Cleanup if not using @Transactional
    // }

    // Helper method to create a mock file
    private MockMultipartFile createMockFile(String name, String originalFilename, String contentType, byte[] content) {
        return new MockMultipartFile(name, originalFilename, contentType, content);
    }

    @Test
    @Transactional // Read-only test, but good practice
    @ApiOperation(value = "测试转发图片上传", tags = "ImgControllerTest")
    void testHandleImageUpload() throws Exception {
        // Arrange
        MockMultipartFile mockFile = createMockFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "test image content".getBytes(StandardCharsets.UTF_8));

        // Mock RestTemplate response
        ImageInfo mockImageInfo = new ImageInfo(); // Populate with expected data
        mockImageInfo.setAddress("Mock Address");
        mockImageInfo.setCaption("Mock Caption");
        mockImageInfo.setTimestamp(new Date());
        mockImageInfo.setAutoTags(Arrays.asList("mockTag1", "mockTag2"));

        String mockResponseJson = objectMapper.writeValueAsString(mockImageInfo);
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/img/post")
                        .file(mockFile))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.address").value(mockImageInfo.getAddress()))
                .andExpect(jsonPath("$.caption").value(mockImageInfo.getCaption()));
    }


    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试添加图片信息", tags = "ImgControllerTest")
    void testUploadImg() throws Exception {
        // Arrange
        MockMultipartFile mockFile = createMockFile("files", "new_image.jpg", MediaType.IMAGE_JPEG_VALUE, "new image data".getBytes(StandardCharsets.UTF_8));

        // Mock RestTemplate response
        ImageInfo mockImageInfo = new ImageInfo();
        mockImageInfo.setAddress("Python Address");
        mockImageInfo.setCaption("Python Caption");
        mockImageInfo.setTimestamp(new Date());
        mockImageInfo.setAutoTags(Arrays.asList("pythonTag", "autoTag"));

        String mockResponseJson = objectMapper.writeValueAsString(mockImageInfo);
        ResponseEntity<String> mockResponseEntity = new ResponseEntity<>(mockResponseJson, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(mockResponseEntity);

        // Mock Minio upload
        when(minioUtilS.upload(any(File.class), eq(ImgPath), anyString())).thenReturn(String.valueOf(true)); // Assuming upload returns boolean

        // Prepare ImgDTO parameters (sent as request params)
        int newUserId = testUserId;
        String imgNameParam = "Uploaded Image";
        Boolean pubParam = true;
        // Integer albumIdParam = null; // Example
        List<String> tagsParam = Arrays.asList("userTag1", "userTag2");
        List<String> peopleParam = Arrays.asList("Bob", "Charlie");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/img/add")
                        .file(mockFile)
                        .param("userId", String.valueOf(newUserId))
                        .param("imgName", imgNameParam)
                        .param("pub", String.valueOf(pubParam))
                        // .param("albumId", String.valueOf(albumIdParam)) // Add if needed
                        .param("tags", String.join(",", tagsParam)) // Assuming tags are comma-separated
                        .param("peoples", String.join(",", peopleParam)) // Assuming people are comma-separated
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.imgId").exists()) // Check if imgId was generated and returned
                .andExpect(jsonPath("$.data.imgName").value(imgNameParam))
                .andExpect(jsonPath("$.data.userId").value(newUserId));
                // Check tags: should include user tags AND python tags if DTO logic combines them

    }

    @Test
    @Transactional // Read-only
    @ApiOperation(value = "测试获取全部图片信息", tags = "ImgControllerTest")
    void testGetImg() throws Exception {
        // Arrange (Data setup in @BeforeEach)
        int offset = 0;
        int limit = 10;

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/img/all")
                        .param("userId", String.valueOf(testUserId)) // Assuming ImgDTO needs userId
                        .param("offset", String.valueOf(offset))
                        .param("limit", String.valueOf(limit))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                // Based on setUp, expect 1 image
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))));
        // Add more checks for tags/people if returned by the service method
    }

    @Test
    @Transactional // Read-only
    @ApiOperation(value = "测试获取某个相册全部图片信息", tags = "ImgControllerTest")
    void testGetImgAlbum() throws Exception {
        // Arrange
        // 1. Create an Album
        // 2. Update testImg1 to belong to this album in setUp or here
        // 3. Set albumId param

        // Example (assuming testImg1 has albumId = 5 after setup)
        // int albumId = 5;
        // testImg1.setAlbumId(albumId);
        // imgMapper.updateById(testImg1);

        // Act & Assert (Modify params and assertions based on actual setup)
        mockMvc.perform(MockMvcRequestBuilders.get("/img/album")
                        .param("userId", String.valueOf(testUserId))
                         .param("albumId", "1")
                        .param("offset", "0")
                        .param("limit", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        // .andExpect(jsonPath("$.data", hasSize(1))) // Expect only images from that album
        // .andExpect(jsonPath("$.data[0].albumId").value(albumId));
    }


    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试删除图片", tags = "ImgControllerTest")
    void testDeleteImg() throws Exception {
        // Arrange (testImg1 created in setUp)
        int imgIdToDelete = testImg1.getImgId();
        assertTrue(testImg1.getValid(), "Image should be valid initially");

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/img/delete")
                        .param("imgId", String.valueOf(imgIdToDelete))
                        .param("userId", String.valueOf(testUserId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                // The service update method likely returns boolean true on success
                .andExpect(jsonPath("$.data").value(true));

        // Verify database state
        Img deletedImg = imgMapper.selectById(imgIdToDelete);
        assertNotNull(deletedImg, "Image should still exist");
        assertFalse(deletedImg.getValid(), "Image valid flag should be false after deletion");
    }

    @Test
    @Transactional // Modifies DB
    @ApiOperation(value = "测试修改图片信息", tags = "ImgControllerTest")
    void testChangeName() throws Exception {
        // Arrange (testImg1 created in setUp)
        int imgIdToChange = testImg1.getImgId();
        String newName = "Changed Name";
        Boolean newPub = false;
        // Integer newAlbumId = ...; // If changing album

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/img/cname")
                        .param("imgId", String.valueOf(imgIdToChange))
                        .param("userId", String.valueOf(testUserId))
                        .param("name", newName)
                        .param("pub", String.valueOf(newPub))
                        // .param("albumId", String.valueOf(newAlbumId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true)); // Assuming service update returns true

        // Verify database state
        Img updatedImg = imgMapper.selectById(imgIdToChange);
        assertNotNull(updatedImg);
        assertEquals(newName, updatedImg.getImgName(), "Image name should be updated");
        assertEquals(newPub, updatedImg.getPub(), "Image pub flag should be updated");
        // assertEquals(newAlbumId, updatedImg.getAlbumId(), "Image albumId should be updated");
    }


    @Test
    @Transactional // Doesn't modify DB based on controller code, but good practice
    @ApiOperation(value = "测试修改图片文件", tags = "ImgControllerTest")
    void testUpgradeImg() throws Exception {
        // Arrange
        int imgIdToUpgrade = testImg1.getImgId();
        MockMultipartFile newMockFile = createMockFile("files", "upgraded.jpg", MediaType.IMAGE_JPEG_VALUE, "upgraded content".getBytes(StandardCharsets.UTF_8));

        // Mock Minio upload
        String expectedMinioName = String.format("%d.jpeg", imgIdToUpgrade);
        when(minioUtilS.upload(any(MultipartFile.class), eq(ImgPath), eq(expectedMinioName))).thenReturn(String.valueOf(true)); // Assuming upload returns boolean

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/img/upgrade")
                        .file(newMockFile)
                        .param("userId", String.valueOf(testUserId)) // Controller method signature requires userId
                        .param("imgId", String.valueOf(imgIdToUpgrade))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200)); // Expecting the result of minioUtilS.upload

//        verify(minioUtilS).upload(any(MultipartFile.class), eq(ImgPath), eq(expectedMinioName));
    }
}
