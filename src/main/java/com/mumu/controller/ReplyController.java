package com.mumu.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.mumu.dto.RepliesDTO;
import com.mumu.dto.ReplyLikesDTO;
import com.mumu.dto.UserDTO;
import com.mumu.entity.Reply;
import com.mumu.entity.Role;
import com.mumu.service.ReplyService;
import com.mumu.service.UserService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-10-23
 */
@RestController
@RequestMapping("/reply")
public class ReplyController {
    @Autowired
    private ReplyService replyService;
    @Autowired
    private UserService userService;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.userPath}")
    private String userImgPath;

    @ApiOperation(value = "获取全部信息（回复）", tags = "测试类")
    @GetMapping("/recordAll")
    public AjaxJson findAll() {
        //Fot testing the concrete class
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.selectList());
    }

    @ApiOperation(value = "通过回复id获取回复信息", tags = "测试类")
    @GetMapping("/recordById")
    public AjaxJson findById(@RequestParam int Id) {
        //Fot testing the concrete class
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.findById(Id));
    }

    @ApiOperation(value = "通过用户id获取回复信息", tags = "测试类")
    @GetMapping("/recordByUserId")
    public AjaxJson findByUserId(@RequestParam int userId) {
        //Fot testing the concrete class
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.findByUserId(userId));
    }

    @ApiOperation(value = "通过对象id获取回复信息", tags = "测试类")
    @GetMapping("/recordByTargetId")
    public AjaxJson findByTargetId(@RequestParam int targetId) {
        //Fot testing the concrete class
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.findByTargetId(targetId));
    }

    @ApiOperation(value = "通过评论id获取回复信息", tags = "测试类")
    @GetMapping("/recordByCommentId")
    public AjaxJson findByCommentId(@RequestParam int commentId) {
        //Fot testing the concrete class
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.findByCommentId(commentId));
    }

    @ApiOperation(value = "添加回复信息", tags = "论坛-回复类")
    @GetMapping("/addOne")
    public AjaxJson addOne(@RequestParam int userId,
                           @RequestParam int targetId,
                           String repliedId,
                           @RequestParam int commentId,
                           @RequestParam String content) {
        if (userService.isBlack(userId)) {
            throw new RuntimeException("You are blacklisted");
        }
        Reply reply = new Reply();
        reply.setUserId(userId);
        reply.setTargetId(targetId);
        if (repliedId != null) {
            reply.setRepliedId(Integer.parseInt(repliedId));
        }
        reply.setCommentId(commentId);
        reply.setReplyContent(content);
        UserDTO user = userService.getUser(userId);
        for (Role role : user.getUserPermission()) {
            if (role.getRoleName().equals("admin")) {
                reply.setReplyValid(1);
            }
        }
        if (replyService.save(reply)) {
            return AjaxJson.getSuccessData(reply.getReplyId());
        } else {
            return AjaxJson.getError("Fail to add a reply");
        }
    }

    @ApiOperation(value = "通过一个评论", tags = "论坛-管理类")
    @SaCheckRole("admin")
    @GetMapping("/passOne")
    public AjaxJson passOne(@RequestParam int Id) {
        Reply reply = replyService.findIfExitById(Id);
        if (reply == null) {
            return AjaxJson.getError("Comment not found");
        } else {
            reply.setReplyValid(1);
            if (replyService.updateById(reply)) {
                return AjaxJson.getSuccess("success pass a reply");
            } else {
                return AjaxJson.getError("Fail to pass a reply");
            }
        }
    }

    @ApiOperation(value = "否决一个评论", tags = "论坛-管理类")
    @SaCheckRole("admin")
    @GetMapping("/defaultOne")
    public AjaxJson defaultOne(@RequestParam int Id) {
        Reply reply = replyService.findIfExitById(Id);
        if (reply == null) {
            return AjaxJson.getError("Comment not found");
        } else {
            reply.setReplyValid(-1);
            if (replyService.updateById(reply)) {
                return AjaxJson.getSuccess("success default a reply");
            } else {
                return AjaxJson.getError("Fail to default a reply");
            }
        }
    }

    @ApiOperation(value = "获取全部未审核的回复信息", tags = "论坛-管理类")
    @GetMapping("/searchAllZero")
    public AjaxJson getAllZero() {
        System.out.println(replyService.getClass().getName());
        List<RepliesDTO> replyDTOS = replyService.searchAllZero();
        replyDTOS.forEach(comment -> comment.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", comment.getUserInformation().getUserId()), userImgPath)));
        return AjaxJson.getSuccessData(replyDTOS);
    }

    @ApiOperation(value = "获取全部未通过的回复信息", tags = "论坛-管理类")
    @GetMapping("/searchAllInValid")
    public AjaxJson getAllInValid() {
        System.out.println(replyService.getClass().getName());
        List<RepliesDTO> replyDTOS = replyService.searchAllInValid();
        replyDTOS.forEach(comment -> comment.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", comment.getUserInformation().getUserId()), userImgPath)));
        return AjaxJson.getSuccessData(replyDTOS);
    }

    @ApiOperation(value = "通过回复id删除回复信息", tags = "论坛-回复类")
    @GetMapping("/deleteById")
    public AjaxJson deleteByReplyId(@RequestParam int Id) {
        System.out.println(replyService.getClass().getName());
        Reply reply = replyService.findIfExitById(Id);
        if (reply == null) {
            return AjaxJson.getError("Reply not found");
        }
        int userId = StpUtil.getLoginIdAsInt();
        if (reply.getUserId() != userId) {
            if (!userService.isAdmin(userId)) {
                return AjaxJson.getError("You don't have the permission to delete this reply");
            }
        }
        return AjaxJson.getSuccessData(replyService.deleteById(Id));
    }

    @ApiOperation(value = "通过用户id删除回复信息", tags = "论坛-回复类")
    @GetMapping("/deleteByUserId")
    public AjaxJson deleteByUserId(@RequestParam int userId) {
        System.out.println(replyService.getClass().getName());
        int id = StpUtil.getLoginIdAsInt();
        if (userId != id) {
            if (!userService.isAdmin(id)) {
                return AjaxJson.getError("You don't have the permission to delete these reply");
            }
        }
        return AjaxJson.getSuccessData(replyService.deleteByUserId(userId));
    }

    @ApiOperation(value = "通过评论回复id删除回复信息", tags = "论坛-回复类")
    @SaCheckRole("admin")
    @GetMapping("/deleteByCommentId")
    public AjaxJson deleteByCommentId(@RequestParam int commentId) {
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.deleteByCommentId(commentId));
    }

    @ApiOperation(value = "通过id获取回复信息", tags = "论坛-回复类")
    @GetMapping("/searchById")
    public AjaxJson getById(@RequestParam int Id) {
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.searchById(Id));
    }

    @ApiOperation(value = "通过用户id获取回复信息（无论通过与否）", tags = "论坛-后台类")
    @GetMapping("/searchByUserId")
    public AjaxJson getByUserId(String userId) {
        System.out.println(replyService.getClass().getName());
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        return AjaxJson.getSuccessData(replyService.searchByUserId(Integer.parseInt(userId)));
    }

    @ApiOperation(value = "通过目标id获取回复信息", tags = "论坛-后台类")
    @GetMapping("/searchByTargetId")
    public AjaxJson getByTargetId(String targetId) {
        System.out.println(replyService.getClass().getName());
        if (targetId == null) {
            targetId = (String) StpUtil.getLoginId();
        }
        return AjaxJson.getSuccessData(replyService.searchByTargetId(Integer.parseInt(targetId)));
    }

    @ApiOperation(value = "通过评论id获取回复信息", tags = "论坛-回复类")
    @GetMapping("/searchByCommentId")
    public AjaxJson getByCommentId(@RequestParam int commentId) {
        System.out.println(replyService.getClass().getName());
        return AjaxJson.getSuccessData(replyService.searchByCommentId(commentId));
    }

    @ApiOperation(value = "获取全部未读的回复信息", tags = "论坛-后台类")
    @GetMapping("/searchUnread")
    public AjaxJson getUnread(String targetId) {
        System.out.println(replyService.getClass().getName());
        if (targetId == null) {
            targetId = (String) StpUtil.getLoginId();
        }
        List<RepliesDTO> repliesDTOS = replyService.searchUnread(Integer.parseInt(targetId));
        repliesDTOS.forEach(repliesDTO -> repliesDTO.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", repliesDTO.getUserInformation().getUserId()), userImgPath)));
        return AjaxJson.getSuccessData(repliesDTOS);
    }

    @ApiOperation(value = "将未读的回复信息标记为已读", tags = "论坛-后台类")
    @GetMapping("/readMessage")
    public AjaxJson readMessage(@RequestParam int replyId) {
        System.out.println(replyService.getClass().getName());
        Reply reply = replyService.findIfExitById(replyId);
        if (reply == null) {
            return AjaxJson.getError("Comment not found");
        } else {
            reply.setReplyRead(1);
            if (replyService.updateById(reply)) {
                return AjaxJson.getSuccess("success read a reply");
            } else {
                return AjaxJson.getError("Fail to read a reply");
            }
        }
    }

    @ApiOperation(value = "获取全部未读的回复点赞", tags = "论坛-后台类")
    @GetMapping("/searchAllUnreadReplyLikes")
    public AjaxJson getAllUnreadReplyLikes(String userId) {
        System.out.println(replyService.getClass().getName());
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        List<ReplyLikesDTO> replyLikesDTOS = replyService.searchAllUnreadLikes(Integer.parseInt(userId));
        replyLikesDTOS.forEach(repliesDTO -> repliesDTO.setImgOfUser(minioUtilS.download(String.format("%s.jpeg", repliesDTO.getUserInformation().getUserId()), userImgPath)));
        return AjaxJson.getSuccessData(replyLikesDTOS);
    }
}
