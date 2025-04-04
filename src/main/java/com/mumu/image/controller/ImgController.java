package com.mumu.image.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mumu.entity.Building;
import com.mumu.entity.BuildingImg;
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
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
            , @RequestParam(required = false, defaultValue = "-1") int offset,
                           @RequestParam(required = false, defaultValue = "10") int limit) {
        return AjaxJson.getSuccessData(imgService.getImagesByTags(imgDTO, offset, limit));
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


    @ApiOperation(value = "添加图片信息", tags = "图片类")
    @PostMapping("/add")
    public AjaxJson uploadImg(ImgDTO imgDTO, MultipartFile files) {
        Img img=new Img(imgDTO);
        imgService.save(img);
        peopleService.checkAndInsertPeople(imgDTO.getUserId(),imgDTO.getPeoples());
        tagService.checkAndInsertTag(imgDTO.getUserId(),imgDTO.getTags());
        List<Integer> peopleId=peopleService.getPeopleIdsByNames(imgDTO.getPeoples(),imgDTO.getUserId());
        List<Integer> tagId=tagService.getTagIdsByNames(imgDTO.getTags(),imgDTO.getUserId());
        imgPeopleService.addImgPeople(imgDTO.getUserId(),img.getImgId(),peopleId);
        imgTagService.addImgTag(imgDTO.getUserId(),img.getImgId(),tagId);
        String imgName = String.format("%d.jpeg", img.getImgId());
        peopleService.checkAndInsertPeople(imgDTO.getUserId(),imgDTO.getPeoples());
        tagService.checkAndInsertTag(imgDTO.getUserId(),imgDTO.getTags());
        // upload img
        minioUtilS.upload(files, ImgPath, imgName);
        return AjaxJson.getSuccessData("success");
    }
}

