package com.mumu.image.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.DTO.ImageInfo;
import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.ImgPeople;
import com.mumu.image.mapper.ImgMapper;
import com.mumu.image.mapper.ImgPeopleMapper;
import com.mumu.image.service.*;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2025-03-14
 */
@RestController
@RequestMapping("/img")
public class ImgController {
    @Autowired
    private MinioUtils minioUtilS;
    @Autowired
    private ImgService imgService;
    @Autowired
    private TagService tagService;
    @Autowired
    private PeopleService peopleService;
    @Autowired
    private ImgPeopleService imgPeopleService;
    @Autowired
    private ImgTagService imgTagService;
    @Value("${minio.uploadImage}")
    private String ImgPath;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${python-backend.endpoint}")
    private String endpoint;
    @ApiOperation(value = "接收图片上传并且将上传图片转发到图片处理服务中", tags = "图片类")
    @PostMapping("/post")
    public ImageInfo handleImageUpload(@RequestParam("file") MultipartFile files) throws IOException {
        // 将接收到的MultipartFile转换为FileSystemResource
        String fileName = UUID.randomUUID().toString()+".jpg";
        try {
            fileName = files.getOriginalFilename();
        }catch (Exception e){}
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        files.transferTo(tempFile);
        FileSystemResource fileResource = new FileSystemResource(tempFile);

        // 创建表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        // 创建请求头，确保设置 multipart/form-data 内容类型
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建HttpEntity，包含body和请求头
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        // 发送POST请求到图片处理服务
        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
// 使用 Jackson 的 ObjectMapper 来将 JSON 字符串转换为 Java 对象
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.getBody(), ImageInfo.class);
    }
    //    @ApiOperation(value = "上传图片", tags = "图片类")
//    @SaCheckRole("admin")
//    @PostMapping("/uploadImg")
//    public AjaxJson upload(@RequestParam int buildingId, MultipartFile files) {
//        System.out.println(minioUtilS.getClass().getName());
//        System.out.println(buildingImgService.getClass().getName());
//        System.out.println(buildingService.getClass().getName());
//        // check
//        Building building = buildingService.oneBuildingById(buildingId);
//        if (building == null)
//            return AjaxJson.getError("Fail to find the building");
//        // get information
//        String largestIdByBuilding = buildingImgService.largestImgIdGroupByBuildingId(buildingId);
//        int largestId = largestIdByBuilding != null ? Integer.parseInt(largestIdByBuilding.split("_")[1].split("\\.")[0]) : 0;
//        String imgName = String.format("%d_%d.jpeg", buildingId, largestId + 1);
//        // upload img
//        minioUtilS.upload(files, buildingImgPath, imgName);
//        // creat buildingImg
//        BuildingImg buildingImg = new BuildingImg();
//        buildingImg.setBuildingId(buildingId);
//        buildingImg.setImgName(imgName);
//        buildingImgService.saveOrUpdate(buildingImg);
//        return AjaxJson.getSuccessData(new String[]{String.valueOf(building.getBuildingImg() + 1), imgName});
//    }
    @ApiOperation(value = "获取全部图片信息", tags = "图片类")
    @GetMapping("/all")
    public AjaxJson getImg(ImgDTO imgDTO
            , @RequestParam(required = false, defaultValue = "-1") int offset
            , @RequestParam(required = false, defaultValue = "10") int limit
    , @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate, @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate) {
        return AjaxJson.getSuccessData(imgService.getImagesByTags(imgDTO, offset, limit,startDate,endDate));
    }
    @ApiOperation(value = "获取某个相册全部图片信息", tags = "相册类")
    @GetMapping("/album")
    public AjaxJson getImgAlbum(ImgDTO imgDTO
            , @RequestParam(required = false, defaultValue = "-1") int offset
            , @RequestParam(required = false, defaultValue = "10") int limit
                                ,@RequestParam(required = false) int albumId
            , @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startDate
            , @RequestParam(required = false)@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endDate
    ) {
        return AjaxJson.getSuccessData(imgService.getImagesByAlbum(imgDTO, offset, limit,startDate,endDate,albumId));
    }
    @ApiOperation(value = "删除图片", tags = "图片类")
    @GetMapping("/delete")
    public AjaxJson deleteImg(int imgId,int userId) {
        return AjaxJson.getSuccessData(imgService.update(new LambdaUpdateWrapper<Img>()
                .eq(Img::getUserId, userId)  // 条件：userId == user1
                .eq(Img::getImgId, imgId)    // 条件：imgId == img1
                .set(Img::getValid, false)   // 更新 valid 字段为 false
                 ));
    }

    @ApiOperation(value = "修改图片信息", tags = "图片类")
    @GetMapping("/cname")
    public AjaxJson changeName(int imgId,int userId,
                               @RequestParam(required = false)String name,
                               @RequestParam(required = false)Boolean pub,
                               @RequestParam(required = false)Integer albumId) {
        return AjaxJson.getSuccessData(imgService.update(new LambdaUpdateWrapper<Img>()
                .eq(Img::getUserId, userId)  // 条件：userId == user1
                .eq(Img::getImgId, imgId)    // 条件：imgId == img1
                .set(name!=null,Img::getImgName, name)   // 更新 valid 字段为 false
                .set(pub!=null,Img::getPub, pub)
                .set(albumId!=null,Img::getAlbumId, albumId)
        ));
    }



    @ApiOperation(value = "添加图片信息", tags = "图片类")
    @PostMapping("/add")
    public AjaxJson uploadImg(ImgDTO imgDTO, MultipartFile files) throws IOException {
        MultipartFile file = files;
        // 将接收到的MultipartFile转换为FileSystemResource
        String fileName = UUID.randomUUID().toString()+".jpg";
        try {
            fileName = files.getOriginalFilename();
        }catch (Exception e){}
        File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
        files.transferTo(tempFile);
        FileSystemResource fileResource = new FileSystemResource(tempFile);

        // 创建表单数据
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);

        // 创建请求头，确保设置 multipart/form-data 内容类型
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建HttpEntity，包含body和请求头
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        // 发送POST请求到图片处理服务
        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
// 使用 Jackson 的 ObjectMapper 来将 JSON 字符串转换为 Java 对象
        ObjectMapper objectMapper = new ObjectMapper();
        ImageInfo res= objectMapper.readValue(response.getBody(), ImageInfo.class);
        if(imgDTO.getImgDate()==null) {
            imgDTO.setImgDate(res.getTimestamp());
        }
        if(imgDTO.getImgPos() == null) {
            imgDTO.setImgPos(res.getAddress());
        }
        if(imgDTO.getImgDescribtion() == null) {
            imgDTO.setImgDescribtion(res.getCaption());
        }
        if(imgDTO.getTags()==null){
            imgDTO.setTags(res.getAutoTags());
        }
        Img img=new Img(imgDTO);
        imgService.save(img);
        imgDTO.setImgId(img.getImgId());
        String imgName = String.format("%d.jpeg", img.getImgId());
        minioUtilS.upload(tempFile, ImgPath, imgName);
        peopleService.checkAndInsertPeople(imgDTO.getUserId(),imgDTO.getPeoples());
        tagService.checkAndInsertTag(imgDTO.getUserId(),imgDTO.getTags());
        List<Integer> peopleId=peopleService.getPeopleIdsByNames(imgDTO.getPeoples(),imgDTO.getUserId());
        List<Integer> tagId=tagService.getTagIdsByNames(imgDTO.getTags(),imgDTO.getUserId());
        imgPeopleService.addImgPeople(imgDTO.getUserId(),img.getImgId(),peopleId);
        imgTagService.addImgTag(imgDTO.getUserId(),img.getImgId(),tagId);
        peopleService.checkAndInsertPeople(imgDTO.getUserId(),imgDTO.getPeoples());
        tagService.checkAndInsertTag(imgDTO.getUserId(),imgDTO.getTags());
        // upload img
        return AjaxJson.getSuccessData(imgDTO);
    }
    @ApiOperation(value = "修改图片", tags = "图片类")
    @PostMapping("/upgrade")
    public AjaxJson upgradeImg(Integer userId,Integer imgId, MultipartFile files) throws IOException {
        String imgName = String.format("%d.jpeg", imgId);

        return AjaxJson.getSuccessData(minioUtilS.upload(files, ImgPath, imgName));
    }
}

