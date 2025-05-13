package com.mumu.image.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.entity.Album;
import com.mumu.image.mapper.AlbumMapper;
// Removed AlbumService import as it's no longer mocked or used directly in these tests
// import com.mumu.image.service.AlbumService;

import io.swagger.annotations.ApiOperation;
import org.junit.jupiter.api.AfterEach; // Import AfterEach for potential cleanup
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// Removed MybatisAutoConfiguration, DataSourceAutoConfiguration etc. imports as they are no longer excluded
import org.springframework.beans.factory.annotation.Autowired;
// Replace @WebMvcTest with @SpringBootTest and @AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// Removed @MockBean imports as we are using real beans now
// import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional; // Import Transactional for automatic rollback
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
// Removed Wrapper import as it's not directly used in test logic anymore
// import com.baomidou.mybatisplus.core.conditions.Wrapper;


// Removed static imports for mockito as it's not used anymore
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // Optional: for printing request/response details


/**
 * AlbumController 的集成测试。
 * 使用 @SpringBootTest 加载完整应用上下文，包含数据源和 MyBatis 配置。
 * 使用 @AutoConfigureMockMvc 来注入 MockMvc。
 * 注意：此测试需要配置测试数据源（例如 H2）并在 src/test/resources 下提供 application.properties/yml。
 */
@SpringBootTest // 加载完整 Spring Boot 上下文
@AutoConfigureMockMvc // 自动配置 MockMvc
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc; // 用于执行模拟 HTTP 请求

    // 不再需要 @MockBean，直接注入真实的 Mapper
    @Autowired
    private AlbumMapper albumMapper;

    // AlbumService 也不再需要 mock，除非 Controller 中确实用到了它
    // @Autowired
    // private AlbumService albumService;

    @Autowired
    private ObjectMapper objectMapper; // 用于 JSON 序列化/反序列化

    // 这些可以作为期望值，但实际数据需要在 @BeforeEach 中插入数据库
    private Album testAlbum1;
    private Album testAlbum2;

    @BeforeEach // 在每个测试方法之前运行
    void setUp() {
        // 重要：在这里准备测试数据库的初始状态
        // 例如，清理相关表并插入测试数据
        // albumMapper.delete(null); // 清理数据（谨慎使用，确保只在测试库操作）

        // 创建要在数据库中插入的测试数据
        testAlbum1 = new Album(1, "Setup Album 1", "Description 1");
        // 注意：Album ID 通常由数据库生成，插入时不指定或指定为 null
        // testAlbum1.setAlbumId(null); // 或者依赖数据库自增
        albumMapper.insert(testAlbum1); // 插入第一条测试数据, ID 会被回填

        testAlbum2 = new Album(1, "Setup Album 2", "Description 2");
        // testAlbum2.setAlbumId(null);
        albumMapper.insert(testAlbum2); // 插入第二条测试数据, ID 会被回填

        // 如果需要，可以在这里重新查询以获取数据库生成的 ID
        // testAlbum1 = albumMapper.selectById(testAlbum1.getAlbumId());
        // testAlbum2 = albumMapper.selectById(testAlbum2.getAlbumId());
    }

    // 可选：如果希望每个测试方法结束后自动回滚数据库更改，可以使用 @Transactional
    // 或者在 @AfterEach 中手动清理数据
    @AfterEach
    void tearDown() {
        // 清理测试数据，避免影响其他测试
        // 例如: albumMapper.delete(new LambdaQueryWrapper<Album>().eq(Album::getUserId, 1));
        // 如果使用了 @Transactional，则不需要手动清理
    }


    @Test
    @Transactional // 推荐：让此测试在事务中运行，结束后自动回滚
    @ApiOperation(value = "测试更新相册名称 (集成)", tags = "AlbumControllerTest")
    void testUpdateAlbum() throws Exception {
        // Arrange: 定义请求参数，使用 setUp 中插入的数据 ID
        int userId = 1;
        // 确保使用 setUp 中插入并可能已回填 ID 的 testAlbum1
        // 如果 setUp 没有重新查询，ID 可能还是 null，需要注意
        // 假设 testAlbum1 在 insert 后 ID 已被 Mybatis-Plus 回填
        if (testAlbum1.getAlbumId() == null) {
            throw new IllegalStateException("Album ID should have been set after insert in setUp");
        }
        int albumIdToUpdate = testAlbum1.getAlbumId(); // 获取数据库生成的 ID
        String newName = "Updated Album Name";
        String newDescription = "Updated description.";

        // 不再需要 mock when(...)

        // 创建请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", String.valueOf(userId));
        params.add("albumId", String.valueOf(albumIdToUpdate));
        params.add("names", newName);
        params.add("albumDescription", newDescription);


        // Act & Assert: 执行 POST 请求并验证响应
        mockMvc.perform(MockMvcRequestBuilders.post("/album/update")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                // 验证 data 是否为 1 (updateById 成功的返回值)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));

        // 可选: 进一步验证数据库状态
        // Album updatedDbAlbum = albumMapper.selectById(albumIdToUpdate);
        // assertNotNull(updatedDbAlbum);
        // assertEquals(newName, updatedDbAlbum.getNames());
        // assertEquals(newDescription, updatedDbAlbum.getAlbumDescription());
    }

    @Test
    @Transactional // 推荐：自动回滚
    @ApiOperation(value = "测试创建新相册 (集成)", tags = "AlbumControllerTest")
    void testInsertAlbum() throws Exception {
        // Arrange
        int userId = 1;
        String name = "Integration Test Album";
        String description = "A brand new album for integration test.";

        // 不再需要 mock when(...)

        // 创建请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", String.valueOf(userId));
        params.add("names", name);
        params.add("albumDescription", description);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/album/new")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                // 验证 data 是否为 1 (insert 成功的返回值)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));

        // 可选: 验证数据库中是否真的插入了数据
        // List<Album> albums = albumMapper.selectList(new LambdaQueryWrapper<Album>().eq(Album::getNames, name));
        // assertEquals(1, albums.size());
        // assertEquals(description, albums.get(0).getAlbumDescription());
    }

    @Test
    @Transactional // 推荐：自动回滚 (如果测试只读，可以不加，但加上无害)
    @ApiOperation(value = "测试获取用户相册 (集成)", tags = "AlbumControllerTest")
    void testGetTag() throws Exception {
        // Arrange
        int userId = 1; // 与 setUp 中插入的数据一致
        // 确保 ID 已设置
        if (testAlbum1.getAlbumId() == null || testAlbum2.getAlbumId() == null) {
            throw new IllegalStateException("Album IDs should have been set after insert in setUp");
        }

        // 不再需要 mock when(...)

        // 创建请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", String.valueOf(userId));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/album/list")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").isArray());
    }

    @Test
    @Transactional // 推荐：自动回滚
    @ApiOperation(value = "测试删除相册 (集成)", tags = "AlbumControllerTest")
    void testDeleteAlbum() throws Exception {
        // Arrange
        int userId = 1;
        // 确保使用 setUp 中插入并可能已回填 ID 的 testAlbum1
        if (testAlbum1.getAlbumId() == null) {
            throw new IllegalStateException("Album ID should have been set after insert in setUp");
        }
        int albumIdToDelete = testAlbum1.getAlbumId(); // 获取数据库生成的 ID

        // 不再需要 mock when(...)

        // 创建请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("userId", String.valueOf(userId));
        params.add("albumId", String.valueOf(albumIdToDelete));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/album/delete")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200))
                // 验证 data 是否为 1 (delete 成功的返回值)
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(1));

        // 可选: 验证数据库中数据是否真的被删除
        // Album deletedAlbum = albumMapper.selectById(albumIdToDelete);
        // assertNull(deletedAlbum);
        // List<Album> remainingAlbums = albumMapper.selectList(new LambdaQueryWrapper<Album>().eq(Album::getUserId, userId));
        // assertEquals(1, remainingAlbums.size()); // 应该只剩下 testAlbum2
    }
}
