package com.mumu.image.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mumu.image.DTO.ImageInfo;
import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.People;
import com.mumu.image.entity.Tag;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Value("${python-backend.process_img_py}")
    private String process_img_py;
    @Value("${python-backend.process_img}")
    private String process_img;
    /**
     * 将 RFC_1123 格式的日期时间字符串 (如 "Sat, 24 May 2025 16:34:15 GMT")
     * 转换为一个 Date 对象，该对象精确到原始输入的小时和分钟，秒和毫秒被清零。
     * 返回的 Date 对象内部将表示原始的 GMT 时间，但秒和毫秒为0。
     *
     * @param rfc1123DateTimeString 输入的日期时间字符串
     * @return 一个 Date 对象，精确到分钟 (秒和毫秒为0)，
     * 如果解析失败则返回 null。
     */
    public static Date convertToDateTimeMinutes(String rfc1123DateTimeString) {
        if (rfc1123DateTimeString == null || rfc1123DateTimeString.isEmpty()) {
            System.err.println("输入日期字符串为 null 或为空。");
            return null;
        }

        // 1. 定义输入日期格式 (RFC_1123_DATE_TIME)
        SimpleDateFormat inputParser = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        inputParser.setTimeZone(TimeZone.getTimeZone("GMT")); // 明确指定输入为GMT

        // 2. 定义一个精确到分钟的格式化器/解析器，用于“截断”秒和毫秒
        //    同样设置为GMT，以确保截断操作在原始时区上下文中进行
        SimpleDateFormat dateTimeMinutesFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        dateTimeMinutesFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            // 3. 解析输入字符串为包含完整时分秒的 Date 对象 (基于GMT)
            Date parsedFullDateTime = inputParser.parse(rfc1123DateTimeString);

            // 4. 将解析出的 Date 对象格式化为 "yyyy-MM-dd HH:mm" 字符串 (仍然是GMT时间)
            String dateTimeMinutesString = dateTimeMinutesFormatter.format(parsedFullDateTime);

            // 5. 将 "yyyy-MM-dd HH:mm" 字符串再次解析为 Date 对象 (基于GMT)
            //    这样得到的 Date 对象，其秒和毫秒部分会在GMT时区下被设为0
            return dateTimeMinutesFormatter.parse(dateTimeMinutesString);

        } catch (ParseException e) {
            System.err.println("日期时间转换错误对于输入: " + rfc1123DateTimeString + " - " + e.getMessage());
            return null;
        }
    }
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
        ResponseEntity<String> response = restTemplate.exchange(process_img_py, HttpMethod.POST, entity, String.class);
// 使用 Jackson 的 ObjectMapper 来将 JSON 字符串转换为 Java 对象
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.printf(response.getBody());
        System.out.println(objectMapper.readValue(response.getBody(), ImageInfo.class));
        return objectMapper.readValue(response.getBody(), ImageInfo.class);
    }

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
                .set(albumId!=null&&albumId!=-1,Img::getAlbumId, albumId)
                .set(albumId != null && albumId.equals(-1), Img::getAlbumId, null)
        ));
    }

    @ApiOperation(value = "获取图片数量", tags = "图片类")
    @GetMapping("/getimgcnt")
    public AjaxJson getimgcnt(int userId) {
        return AjaxJson.getSuccessData(imgService.count(new LambdaQueryWrapper<Img>()
                .eq(Img::getUserId, userId)
        ));
    }

    @ApiOperation(value = "获取people数量", tags = "图片类")
    @GetMapping("/getpeoplecnt")
    public AjaxJson getpeoplecnt(int userId) {
        return AjaxJson.getSuccessData(peopleService.count(new LambdaQueryWrapper<People>()
                .eq(People::getUserId, userId)
        ));
    }
    @ApiOperation(value = "获取tag数量", tags = "图片类")
    @GetMapping("/gettagcnt")
    public AjaxJson gettagcnt(int userId) {
        return AjaxJson.getSuccessData(tagService.count(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getUserId, userId)
        ));
    }

    @ApiOperation(value = "添加图片带信息", tags = "图片类")
    @PostMapping("/add")
    public AjaxJson uploadImg(ImgDTO imgDTO, MultipartFile files) throws IOException, ParseException {
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
        ResponseEntity<String> response = restTemplate.exchange(process_img_py, HttpMethod.POST, entity, String.class);

// 使用 Jackson 的 ObjectMapper 来将 JSON 字符串转换为 Java 对象
        ObjectMapper objectMapper = new ObjectMapper();
        ImageInfo res= objectMapper.readValue(response.getBody(), ImageInfo.class);
        if(imgDTO.getImgDate()==null) {
                imgDTO.setImgDate(res.getTimestamp());
        }
        if(imgDTO.getImgDate()==null) {
            try {
                imgDTO.setImgDate(convertToDateTimeMinutes(response.getHeaders().get("date").get(0)));
            }catch (Exception e){}
        }
        if(imgDTO.getImgName()==null) {
            imgDTO.setImgName(fileName);
        }
        if(imgDTO.getImgDate()==null) {
            imgDTO.setImgDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"));
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
        if(imgDTO.getPeoples()==null){
            imgDTO.setPeoples(res.getPersonLabel());
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

    @ApiOperation(value = "添加图片无添加图片信息", tags = "图片类")
    @PostMapping("/addnoinfo")
    public AjaxJson uploadImgNoInfo(ImgDTO imgDTO, MultipartFile files) throws IOException, ParseException, InterruptedException {
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
        ResponseEntity<String> response = restTemplate.exchange(process_img, HttpMethod.POST, entity, String.class);
        System.out.println(response);

// 使用 Jackson 的 ObjectMapper 来将 JSON 字符串转换为 Java 对象
        ObjectMapper objectMapper = new ObjectMapper();
        ImageInfo res= objectMapper.readValue(response.getBody(), ImageInfo.class);
        if(imgDTO.getImgDate()==null) {
            imgDTO.setImgDate(res.getTimestamp());
        }
        if(imgDTO.getImgDate()==null) {
            try {
                imgDTO.setImgDate(convertToDateTimeMinutes(response.getHeaders().get("date").get(0)));
            }catch (Exception e){}
        }
        if(imgDTO.getImgName()==null) {
            imgDTO.setImgName(fileName);
        }
        if(imgDTO.getImgDate()==null) {
            imgDTO.setImgDate(new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"));
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
        if(imgDTO.getPeoples()==null){
            imgDTO.setPeoples(res.getPersonLabel());
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
        Thread.sleep(1000);
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

