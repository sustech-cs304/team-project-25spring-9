package com.mumu.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.dto.*;
import com.mumu.entity.Building;
import com.mumu.entity.BuildingImg;
import com.mumu.service.*;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
@RestController
@RequestMapping("/building")
public class BuildingController {
    @Autowired
    private BuildingImgService buildingImgService;
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private CommentImgService commentImgService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentController commentController;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.buildingPath}")
    private String buildingImgPath;
    @Value("${minio.commentPath}")
    private String commentImgPath;
    @Value("${minio.userPath}")
    private String userImgPath;

    @ApiOperation(value = "查询所有信息（建筑、评论、回复）", tags = "测试类")
    @GetMapping("/recordAll")
    public AjaxJson findAll() {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        return AjaxJson.getSuccessData(buildingService.selectList());
    }

    @ApiOperation(value = "根据建筑id查询所有信息（建筑、评论、回复）", tags = "测试类")
    @GetMapping("/recordById")
    public AjaxJson findById(@RequestParam int Id) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        return AjaxJson.getSuccessData(buildingService.findById(Id));
    }

    @ApiOperation(value = "添加建筑信息", tags = "论坛-建筑类")
    @SaCheckRole("admin")
    @GetMapping("/addOne")
    public AjaxJson addOne(@RequestParam String buildingName,
                           String description,
                           @RequestParam double X,
                           @RequestParam double Y) {
        if (buildingService.getIdByName(buildingName) != -1)
            return AjaxJson.getError("The building is already exist");
        String name = buildingService.getIdByLocation(X, Y);
        if (name != null)
            return AjaxJson.getError("The building " + name + " is located here");
        Building building = new Building();
        building.setBuildingName(buildingName);
        if (description != null)
            building.setBuildingDescription(description);
        building.setBuildingX(X);
        building.setBuildingY(Y);
        if (buildingService.saveOrUpdate(building)) {
            commentController.addOne(0, building.getBuildingId(), "初始评论");
            return AjaxJson.getSuccessData(building.getBuildingId());
        } else {
            return AjaxJson.getError("Fail to add a building");
        }
    }

    @ApiOperation(value = "编辑建筑信息", tags = "论坛-建筑类")
    @SaCheckRole("admin")
    @GetMapping("/editOne")
    public AjaxJson editOne(@RequestParam int buildingId,
                            String buildingName,
                            String description,
                            String X,
                            String Y) {
        Building building = buildingService.oneBuildingById(buildingId);
        if (building == null)
            return AjaxJson.getError("The building is not exist");
        if (buildingName != null)
            building.setBuildingName(buildingName);
        if (description != null)
            building.setBuildingDescription(description);
        if (X != null)
            building.setBuildingX(Double.parseDouble(X));
        if (Y != null)
            building.setBuildingY(Double.parseDouble(Y));
        if (buildingService.updateById(building)) {
            return AjaxJson.getSuccess("Success to edit the description");
        } else {
            return AjaxJson.getError("Fail to edit the description");
        }
    }

    @ApiOperation(value = "上传建筑图片", tags = "论坛-建筑类")
    @SaCheckRole("admin")
    @PostMapping("/uploadImg")
    public AjaxJson upload(@RequestParam int buildingId, MultipartFile files) {
        System.out.println(minioUtilS.getClass().getName());
        System.out.println(buildingImgService.getClass().getName());
        System.out.println(buildingService.getClass().getName());
        // check
        Building building = buildingService.oneBuildingById(buildingId);
        if (building == null)
            return AjaxJson.getError("Fail to find the building");
        // get information
        String largestIdByBuilding = buildingImgService.largestImgIdGroupByBuildingId(buildingId);
        int largestId = largestIdByBuilding != null ? Integer.parseInt(largestIdByBuilding.split("_")[1].split("\\.")[0]) : 0;
        String imgName = String.format("%d_%d.jpeg", buildingId, largestId + 1);
        // upload img
        minioUtilS.upload(files, buildingImgPath, imgName);
        // creat buildingImg
        BuildingImg buildingImg = new BuildingImg();
        buildingImg.setBuildingId(buildingId);
        buildingImg.setImgName(imgName);
        buildingImgService.saveOrUpdate(buildingImg);
        return AjaxJson.getSuccessData(new String[]{String.valueOf(building.getBuildingImg() + 1), imgName});
    }

    @ApiOperation(value = "下载建筑图片", tags = "论坛-建筑类")
    @GetMapping("/downloadImg")
    public AjaxJson download(@RequestParam int buildingId) {
        System.out.println(buildingService.getClass().getName());
        System.out.println(buildingImgService.getClass().getName());
        System.out.println(minioUtilS.getClass().getName());
        // check
        Building building = buildingService.oneBuildingById(buildingId);
        if (building == null)
            return AjaxJson.getError("Fail to find the building");
        // get all img
        List<String> allBuildingImg = buildingImgService.allBuildingImg(buildingId);
        List<BuildingImgDTO> imgList = new ArrayList<>();
        for (String imgName : allBuildingImg) {
            imgList.add(
                    new BuildingImgDTO(
                            imgName,
                            minioUtilS.download(imgName, buildingImgPath)
                    )
            );
        }
        return AjaxJson.getSuccessData(imgList);
    }

    @ApiOperation(value = "通过建筑id删除所有相关信息（建筑、评论、回复）", tags = "论坛-建筑类")
    @SaCheckRole("admin")
    @GetMapping("/deleteById")
    public AjaxJson deleteById(@RequestParam int Id) {
        System.out.println(buildingService.getClass().getName());
        if (!buildingImgService.deleteImgByBuildingId(Id).equals("删除文件成功")) {
            return AjaxJson.getError("Fail to delete the building img");
        }
        return AjaxJson.getSuccessData(buildingService.deleteById(Id));
    }

    @ApiOperation(value = "获取所有建筑的id和名称", tags = "论坛-建筑类")
    @GetMapping("/searchAll")
    public AjaxJson allBuildingsIdAndName() {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        return AjaxJson.getSuccessData(buildingService.allBuildings());
    }

    @ApiOperation(value = "获取所有建筑的名称和第一张图片", tags = "论坛-建筑类")
    @GetMapping("/searchAllFirst")
    public AjaxJson allBuildingsOneImg() {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        System.out.println(buildingImgService.getClass().getName());
        System.out.println(minioUtilS.getClass().getName());
        List<BuildingNameDTO> buildingsInfoWithImg = buildingService.allBuildingsFirst();
        for (BuildingNameDTO buildingNameDTO : buildingsInfoWithImg) {
            Building building = buildingService.oneBuildingByName(buildingNameDTO.getBuildingName());
            if (building.getBuildingImg() != 0) {
                String imgName = buildingImgService.firstImgIdGroupByBuildingId(building.getBuildingId());
                ResponseEntity<byte[]> Img = minioUtilS.download(imgName, buildingImgPath);
                buildingNameDTO.setImg(Img);
            }
        }
        return AjaxJson.getSuccessData(buildingsInfoWithImg);
    }

    @ApiOperation(value = "获取所有建筑的id，名称，坐标", tags = "论坛-建筑类")
    @GetMapping("/searchAllLocation")
    public AjaxJson allBuildingsLocation() {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        return AjaxJson.getSuccessData(buildingService.allBuildingsLocation());
    }

    @ApiOperation(value = "获取所有建筑的全部信息（不含图片）", tags = "论坛-建筑类")
    @GetMapping("/searchAllInfo")
    public AjaxJson allBuildingsInformation() {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        return AjaxJson.getSuccessData(buildingService.allBuildingsInfo());
    }

    @ApiOperation(value = "获取所有建筑的全部信息以及最后一张图片", tags = "论坛-建筑类")
    @GetMapping("/searchAllInfoWithImg")
    public AjaxJson allBuildingsInformationWithImg() {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        System.out.println(buildingImgService.getClass().getName());
        System.out.println(minioUtilS.getClass().getName());
        List<BuildingWithImgDTO> buildingsInfoWithImg = buildingService.allBuildingsInfoWithImg();
        for (BuildingWithImgDTO buildingWithImgDTO : buildingsInfoWithImg) {
            if (buildingWithImgDTO.getBuilding().getBuildingImg() != 0) {
                String imgName = buildingImgService.largestImgIdGroupByBuildingId(buildingWithImgDTO.getBuilding().getBuildingId());
                ResponseEntity<byte[]> Img = minioUtilS.download(imgName, buildingImgPath);
                buildingWithImgDTO.setImg(Img);
            }
        }
        return AjaxJson.getSuccessData(buildingsInfoWithImg);
    }

    @ApiOperation(value = "通过建筑id获取建筑信息（包含图片）", tags = "论坛-建筑类")
    @GetMapping("/searchById")
    public AjaxJson oneBuildingById(@RequestParam int Id) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        Building building = buildingService.oneBuildingById(Id);
        if (building == null)
            return AjaxJson.getError("Fail to find the building");
        List<String> allBuildingImg = buildingImgService.allBuildingImg(Id);
        List<BuildingImgDTO> imgList = new ArrayList<>();
        for (String imgName : allBuildingImg) {
            imgList.add(
                    new BuildingImgDTO(
                            imgName,
                            minioUtilS.download(imgName, buildingImgPath)
                    )
            );
        }
        return AjaxJson.getSuccessData(new BuildingWithImgListDTO(
                buildingService.oneBuildingById(Id),
                imgList
        ));
    }

    @ApiOperation(value = "通过建筑id获取建筑信息（包含图片）以及有关评论、回复（均按点赞数量降序排序）", tags = "论坛-建筑类")
    @GetMapping("/searchByIdWithComments")
    public AjaxJson oneBuildingByIdWithComments(@RequestParam int Id) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        Building building = buildingService.oneBuildingById(Id);
        if (building == null)
            return AjaxJson.getError("Fail to find the building");
        BuildingCommentsDTO buildingCommentsDTOS = buildingService.commentsRelatedById(Id);
        // 下载建筑图片
        List<String> allBuildingImg = buildingImgService.allBuildingImg(Id);
        List<BuildingImgDTO> buildingImgList = allBuildingImg.stream().map(imgName -> new BuildingImgDTO(
                imgName,
                minioUtilS.download(imgName, buildingImgPath)
        )).collect(Collectors.toList());
        buildingCommentsDTOS.setImgOfBuilding(buildingImgList);
        // 按照时间先后排序评论
        buildingCommentsDTOS.setComments(
                buildingCommentsDTOS.getComments().stream()
                        .sorted(Comparator.comparing(CommentsDTO::getCommentTime))
                        .filter(commentsDTO -> !commentsDTO.getCommentContent().equals("初始评论"))
                        .collect(Collectors.toList())
        );
        buildingCommentsDTOS.getComments().forEach(commentsDTO -> {
            // 下载评论图片
            List<String> allCommentImg = commentImgService.allCommentImg(commentsDTO.getCommentId());
            List<CommentImgDTO> commentImgList = allCommentImg.stream().map(imgName -> new CommentImgDTO(
                    imgName,
                    minioUtilS.download(imgName, commentImgPath)
            )).collect(Collectors.toList());
            commentsDTO.setImgOfComment(commentImgList);
            // 下载评论用户头像
            commentsDTO.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", commentsDTO.getUserInformation().getUserId()), userImgPath));
            // 按照时间先后排序回复
            commentsDTO.setReplies(
                    commentsDTO.getReplies().stream()
                            .sorted(Comparator.comparing(RepliesDTO::getReplyTime))
                            .filter(repliesDTO -> !repliesDTO.getReplyContent().equals("初始回复"))
                            .collect(Collectors.toList())
            );
            // 下载回复用户头像
            commentsDTO.getReplies().forEach(repliesDTO -> repliesDTO.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", repliesDTO.getUserInformation().getUserId()), userImgPath)));
        });
        return AjaxJson.getSuccessData(buildingCommentsDTOS);
    }

    @ApiOperation(value = "通过建筑名称获取建筑信息（包含图片）", tags = "论坛-建筑类")
    @GetMapping("/searchByName")
    public AjaxJson oneBuildingByName(@RequestParam String Name) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        int Id = buildingService.getIdByName(Name);
        if (Id == -1) {
            return AjaxJson.getError("No building found");
        }
        return oneBuildingById(Id);
    }

    @ApiOperation(value = "通过建筑名称获取建筑信息（包含图片）以及有关评论、回复（均按点赞数量降序排序）", tags = "论坛-建筑类")
    @GetMapping("/searchByNameWithComments")
    public AjaxJson oneBuildingByNameWithComments(@RequestParam String Name) {
        System.out.println(buildingService.getClass().getName());
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        int Id = buildingService.getIdByName(Name);
        if (Id == -1) {
            return AjaxJson.getError("No building found");
        }
        return oneBuildingByIdWithComments(Id);
    }

    @ApiOperation(value = "热度前十（按照评论数量排序）的建筑信息（不含图片）", tags = "论坛-建筑类")
    @GetMapping("/searchTopOrderByComments")
    public AjaxJson topCommentsOrderByLikes(@RequestParam int num) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        if (num < 0) {
            return AjaxJson.getError("num should be positive");
        }
        return AjaxJson.getSuccessData(buildingService.topBuildingsOrderByComments(num));
    }

    @ApiOperation(value = "关键词检索（建筑、评论、回复当中所有文字匹配的项的id，只有id！），用来分割的分隔符：!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~", tags = "论坛-全局类")
    @GetMapping("/searchByKeyWords")
    public AjaxJson searchByKeyWords(@RequestParam String keyWords) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        System.out.println(commentService.getClass().getName());
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(
                new GlobalDTO(
                        buildingService.searchByKeyWords(keyWords),
                        commentService.searchByKeyWords(keyWords),
                        replyService.searchByKeyWords(keyWords)
                )
        );
    }

    @ApiOperation(value = "关键词检索，只返回建筑名称匹配的建筑id", tags = "论坛-全局类")
    @GetMapping("/searchBuildingByKeyWords")
    public AjaxJson searchBuildingByKeyWords(@RequestParam String keyWords) {
        //Fot testing the concrete class
        System.out.println(buildingService.getClass().getName());
        return AjaxJson.getSuccessData(buildingService.searchBuildingByKeyWords(keyWords));
    }
}
