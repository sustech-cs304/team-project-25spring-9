package com.mumu.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.mumu.dto.*;
import com.mumu.entity.*;
import com.mumu.service.CommentImgService;
import com.mumu.service.CommentService;
import com.mumu.service.UserService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @since 2023-10-14
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private UserService userService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private ReplyController replyController;
    @Autowired
    private CommentImgService commentImgService;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.userPath}")
    private String userImgPath;
    @Value("${minio.commentPath}")
    private String commentImgPath;

    @ApiOperation(value = "获取所有信息（评论、回复）", tags = "测试类")
    @GetMapping("/recordAll")
    public AjaxJson findAll() {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        return AjaxJson.getSuccessData(commentService.selectList());
    }

    @ApiOperation(value = "通过评论id获取所有信息（评论、回复）", tags = "测试类")
    @GetMapping("/recordById")
    public AjaxJson findById(@RequestParam int Id) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        return AjaxJson.getSuccessData(commentService.findById(Id));
    }

    @ApiOperation(value = "通过用户id获取所有信息（评论、回复）", tags = "测试类")
    @GetMapping("/recordByUserId")
    public AjaxJson findByUserId(@RequestParam int userId) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        return AjaxJson.getSuccessData(commentService.findByUserId(userId));
    }

    @ApiOperation(value = "通过评论id获取所有信息（评论、回复）", tags = "测试类")
    @GetMapping("/recordByBuildingId")
    public AjaxJson findByBuildingId(@RequestParam int buildingId) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        return AjaxJson.getSuccessData(commentService.findByBuildingId(buildingId));
    }

    @ApiOperation(value = "添加一个评论", tags = "论坛-评论类")
    @GetMapping("/addOne")
    public AjaxJson addOne(@RequestParam int userId,
                           @RequestParam int buildingId,
                           @RequestParam String content) {
        if (userService.isBlack(userId)) {
            throw new RuntimeException("You are blacklisted");
        }
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setBuildingId(buildingId);
        comment.setCommentContent(content);
        if (StpUtil.hasPermission("black"))
            throw new RuntimeException("You are blacklisted");
        UserDTO user = userService.getUser(userId);
        for (Role role : user.getUserPermission()) {
            if (role.getRoleName().equals("admin")) {
                comment.setCommentValid(1);
            }
        }
        if (commentService.save(comment)) {
            replyController.addOne(0, 0, null, comment.getCommentId(), "初始回复");
            return AjaxJson.getSuccessData(comment.getCommentId());
        } else {
            return AjaxJson.getError("Fail to add a comment");
        }
    }

    @ApiOperation(value = "上传评论图片", tags = "论坛-评论类")
    @PostMapping("/uploadImg")
    public AjaxJson upload(@RequestParam int commentId, MultipartFile files) {
        System.out.println(minioUtilS.getClass().getName());
        System.out.println(commentImgService.getClass().getName());
        System.out.println(commentService.getClass().getName());
        // check
        Comment comment = commentService.oneCommentById(commentId);
        if (comment == null)
            return AjaxJson.getError("Fail to find the comment");
        // get information
        String largestIdByComment = commentImgService.largestImgIdGroupByCommentId(commentId);
        int largestId = largestIdByComment != null ? Integer.parseInt(largestIdByComment.split("_")[1].split("\\.")[0]) : 0;
        String imgName = String.format("%d_%d.jpeg", commentId, largestId + 1);
        // upload img
        minioUtilS.upload(files, commentImgPath, imgName);
        // creat commentImg
        CommentImg commentImg = new CommentImg();
        commentImg.setCommentId(commentId);
        commentImg.setImgName(imgName);
        commentImgService.saveOrUpdate(commentImg);
        return AjaxJson.getSuccessData(new String[]{String.valueOf(comment.getCommentImg() + 1), imgName});
    }

    @ApiOperation(value = "下载评论图片", tags = "论坛-评论类")
    @GetMapping("/downloadImg")
    public AjaxJson download(@RequestParam int commentId) {
        System.out.println(commentService.getClass().getName());
        System.out.println(commentImgService.getClass().getName());
        System.out.println(minioUtilS.getClass().getName());
        // check
        Comment comment = commentService.oneCommentById(commentId);
        if (comment == null)
            return AjaxJson.getError("Fail to find the comment");
        // get all img
        List<String> allCommentImg = commentImgService.allCommentImg(commentId);
        List<CommentImgDTO> imgList = new ArrayList<>();
        for (String imgName : allCommentImg) {
            imgList.add(
                    new CommentImgDTO(
                            imgName,
                            minioUtilS.download(imgName, commentImgPath)
                    )
            );
        }
        return AjaxJson.getSuccessData(imgList);
    }

    @ApiOperation(value = "通过一个评论", tags = "论坛-管理类")
    @SaCheckRole("admin")
    @GetMapping("/passOne")
    public AjaxJson passOne(@RequestParam int Id) {
        Comment comment = commentService.findIfExitById(Id);
        if (comment == null) {
            return AjaxJson.getError("Comment not found");
        } else {
            comment.setCommentValid(1);
            if (commentService.updateById(comment)) {
                return AjaxJson.getSuccess("success pass a comment");
            } else {
                return AjaxJson.getError("Fail to pass a comment");
            }
        }
    }

    @ApiOperation(value = "否决一个评论", tags = "论坛-管理类")
    @SaCheckRole("admin")
    @GetMapping("/defaultOne")
    public AjaxJson defaultOne(@RequestParam int Id) {
        Comment comment = commentService.findIfExitById(Id);
        if (comment == null) {
            return AjaxJson.getError("Comment not found");
        } else {
            comment.setCommentValid(-1);
            if (commentService.updateById(comment)) {
                return AjaxJson.getSuccess("success default a comment");
            } else {
                return AjaxJson.getError("Fail to default a comment");
            }
        }
    }

    @ApiOperation(value = "获取全部未审核的回复信息", tags = "论坛-管理类")
    @GetMapping("/searchAllZero")
    public AjaxJson getAllUnchecked() {
        System.out.println(commentService.getClass().getName());
        List<CommentAdminDTO> commentAdminDTO = commentService.searchAllUnchecked();
        commentAdminDTO.forEach(comment -> comment.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", comment.getUserInformation().getUserId()), userImgPath)));
        // 下载评论图片
        commentAdminDTO.forEach(adminDTO -> {
            List<String> allCommentImg = commentImgService.allCommentImg(adminDTO.getCommentId());
            List<CommentImgDTO> imgList = allCommentImg.stream().map(imgName -> new CommentImgDTO(
                    imgName,
                    minioUtilS.download(imgName, commentImgPath)
            )).collect(Collectors.toList());
            adminDTO.setImgOfComment(imgList);
        });
        return AjaxJson.getSuccessData(commentAdminDTO);
    }

    @ApiOperation(value = "获取全部未通过的回复信息", tags = "论坛-管理类")
    @GetMapping("/searchAllInValid")
    public AjaxJson getAllInValid() {
        System.out.println(commentService.getClass().getName());
        List<CommentAdminDTO> commentAdminDTO = commentService.searchAllInValid();
        commentAdminDTO.forEach(comment -> comment.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", comment.getUserInformation().getUserId()), userImgPath)));
        // 下载评论图片
        commentAdminDTO.forEach(adminDTO -> {
            List<String> allCommentImg = commentImgService.allCommentImg(adminDTO.getCommentId());
            List<CommentImgDTO> imgList = allCommentImg.stream().map(imgName -> new CommentImgDTO(
                    imgName,
                    minioUtilS.download(imgName, commentImgPath)
            )).collect(Collectors.toList());
            adminDTO.setImgOfComment(imgList);
        });
        return AjaxJson.getSuccessData(commentAdminDTO);
    }

    @ApiOperation(value = "通过评论id删除评论", tags = "论坛-评论类")
    @GetMapping("/deleteById")
    public AjaxJson deleteByCommentId(@RequestParam int Id) {
        System.out.println(commentService.getClass().getName());
        Comment comment = commentService.findIfExitById(Id);
        if (comment == null) {
            return AjaxJson.getError("Comment not found");
        }
        int userId = StpUtil.getLoginIdAsInt();
        if (comment.getUserId() != userId) {
            if (!userService.isAdmin(userId)) {
                return AjaxJson.getError("You don't have the permission to delete this comment");
            }
        }
        if (!commentImgService.deleteImgByCommentId(Id).equals("删除文件成功")) {
            return AjaxJson.getError("Fail to delete the img");
        }
        return AjaxJson.getSuccessData(commentService.deleteById(Id));
    }

    @ApiOperation(value = "通过用户id删除评论", tags = "论坛-评论类")
    @GetMapping("/deleteByUserId")
    public AjaxJson deleteByUserId(@RequestParam int userId) {
        System.out.println(commentService.getClass().getName());
        int ID = StpUtil.getLoginIdAsInt();
        if (userId != ID) {
            if (!userService.isAdmin(ID)) {
                return AjaxJson.getError("You don't have the permission to delete these comment");
            }
        }
        List<Integer> commentList = commentService.getIdByUserId(userId);
        for (Integer id : commentList) {
            if (!commentImgService.deleteImgByCommentId(id).equals("删除文件成功")) {
                return AjaxJson.getError("Fail to delete the building img");
            }
        }
        return AjaxJson.getSuccessData(commentService.deleteByUserId(userId));
    }

    @ApiOperation(value = "通过建筑id删除评论", tags = "论坛-评论类")
    @SaCheckRole("admin")
    @GetMapping("/deleteByBuildingId")
    public AjaxJson deleteByBuildingId(@RequestParam int buildingId) {
        System.out.println(commentService.getClass().getName());
        List<Integer> commentList = commentService.getIdByBuildingId(buildingId);
        for (Integer id : commentList) {
            if (!commentImgService.deleteImgByCommentId(id).equals("删除文件成功")) {
                return AjaxJson.getError("Fail to delete the building img");
            }
        }
        return AjaxJson.getSuccessData(commentService.deleteByBuildingId(buildingId));
    }

    @ApiOperation(value = "所有话题的id和内容（按照点赞数量排序）", tags = "论坛-评论类")
    @GetMapping("/searchAllOrderByLikes")
    public AjaxJson allCommentsOrderByLikes() {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        return AjaxJson.getSuccessData(commentService.allCommentsOrderByLikes());
    }

    @ApiOperation(value = "所有话题的id和内容（按照回复数量排序）", tags = "论坛-评论类")
    @GetMapping("/searchAllOrderByReplies")
    public AjaxJson allCommentsOrderByReplies() {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        return AjaxJson.getSuccessData(commentService.allCommentsOrderByReplies());
    }

    @ApiOperation(value = "评论总数以及热度前十（按照点赞数量排序）", tags = "论坛-评论类")
    @GetMapping("/searchTopOrderByLikes")
    // 评论总数以及热度前十（按照点赞数量排序）
    public AjaxJson topCommentsOrderByLikes(@RequestParam int num) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        if (num < 0) {
            return AjaxJson.getError("num must be positive");
        }
        return AjaxJson.getSuccessData(
                new CommentsCountingDTO(
                        commentService.countAllValid(),
                        commentService.topCommentsOrderByLikes(num)
                )
        );
    }

    @ApiOperation(value = "评论总数以及热度前十（按照回复数量排序）", tags = "论坛-评论类")
    @GetMapping("/searchTopOrderByReplies")
    // 评论总数以及热度前十（按照评论数量排序）
    public AjaxJson topCommentsOrderByReplies(@RequestParam int num) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        if (num < 0) {
            return AjaxJson.getError("num must be positive");
        }
        return AjaxJson.getSuccessData(
                new CommentsCountingDTO(
                        commentService.countAllValid(),
                        commentService.topCommentsOrderByReplies(num)
                )
        );
    }

    @ApiOperation(value = "指定评论信息，包含回复以及图片", tags = "论坛-评论类")
    @GetMapping("/searchById")
    public AjaxJson oneComment(@RequestParam int Id) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        if (commentService.findIfExitById(Id) == null) {
            return AjaxJson.getError("Comment not found");
        }
        CommentsDTO commentsDTO = commentService.oneComment(Id);
        // 下载评论图片
        List<String> allCommentImg = commentImgService.allCommentImg(commentsDTO.getCommentId());
        List<CommentImgDTO> imgList = allCommentImg.stream().map(imgName -> new CommentImgDTO(
                imgName,
                minioUtilS.download(imgName, commentImgPath)
        )).collect(Collectors.toList());
        commentsDTO.setImgOfComment(imgList);
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
        return AjaxJson.getSuccessData(commentsDTO);
    }

    @ApiOperation(value = "用户id获取评论信息，无论通过与否", tags = "论坛-后台类")
    @GetMapping("/searchByUserId")
    public AjaxJson searchByUserId(String userId) {
        //Fot testing the concrete class
        System.out.println(commentService.getClass().getName());
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        List<CommentUserDTO> commentUserDTO = commentService.searchCommentsByUserId(Integer.parseInt(userId));
        commentUserDTO.forEach(dto -> dto.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", dto.getUserInformation().getUserId()), userImgPath)));
        // 下载评论图片
        commentUserDTO.forEach(dto -> {
            List<String> allCommentImg = commentImgService.allCommentImg(dto.getCommentId());
            List<CommentImgDTO> imgList = allCommentImg.stream().map(imgName -> new CommentImgDTO(
                    imgName,
                    minioUtilS.download(imgName, commentImgPath)
            )).collect(Collectors.toList());
            dto.setImgOfComment(imgList);
        });
        return AjaxJson.getSuccessData(commentUserDTO);
    }

    @ApiOperation(value = "获取全部未读的评论点赞", tags = "论坛-后台类")
    @GetMapping("/searchAllUnreadCommentLikes")
    public AjaxJson getAllUnreadCommentLikes(String userId) {
        System.out.println(commentService.getClass().getName());
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        List<CommentLikesDTO> commentLikesDTO = commentService.searchAllUnreadLikes(Integer.parseInt(userId));
        // 下载评论用户头像
        commentLikesDTO.forEach(dto -> dto.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", dto.getUserInformation().getUserId()), userImgPath)));
        // 下载评论图片
        commentLikesDTO.forEach(dto -> {
            List<String> allCommentImg = commentImgService.allCommentImg(dto.getCommentId());
            List<CommentImgDTO> imgList = allCommentImg.stream().map(imgName -> new CommentImgDTO(
                    imgName,
                    minioUtilS.download(imgName, commentImgPath)
            )).collect(Collectors.toList());
            dto.setImgOfComment(imgList);
        });
        return AjaxJson.getSuccessData(commentLikesDTO);
    }

    @ApiOperation(value = "获取全部未读的点赞，评论和回复都有", tags = "论坛-后台类")
    @GetMapping("/searchAllUnreadLikes")
    public AjaxJson getAllUnreadLikes(String userId) {
        System.out.println(commentService.getClass().getName());
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        List<LikesDTO> likesDTO = commentService.allUnreadLikes(Integer.parseInt(userId));
        List<LikesDTO> likesDTOList = likesDTO.stream().sorted(Comparator.comparing(LikesDTO::getLikeTime).reversed()).toList();
        likesDTOList.forEach(dto -> dto.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", dto.getUserInformation().getUserId()), userImgPath)));
        return AjaxJson.getSuccessData(likesDTOList);
    }
}
