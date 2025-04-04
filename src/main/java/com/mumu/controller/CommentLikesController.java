package com.mumu.controller;


import com.mumu.entity.CommentLikes;
import com.mumu.service.CommentLikesService;
import com.mumu.service.CommentService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-11-05
 */
@RestController
@RequestMapping("/commentLikes")
public class CommentLikesController {
    @Autowired
    private CommentLikesService commentLikesService;
    @Autowired
    CommentService commentService;

    @ApiOperation(value = "添加/取消评论点赞，返回操作后点赞数", tags = "论坛-评论类")
    @GetMapping("/addOne")
    public AjaxJson addOne(@RequestParam int userId, @RequestParam int commentId) {
        CommentLikes commentLikes = new CommentLikes();
        commentLikes.setUserId(userId);
        commentLikes.setCommentId(commentId);
        CommentLikes temp = commentLikesService.searchIfExit(commentLikes);
        if (temp != null) {
            deleteByCommentLikesId(temp.getId());
            return AjaxJson.getSuccessData(commentService.searchLikesById(temp.getCommentId()));
        } else {
            if (commentLikesService.saveOrUpdate(commentLikes)) {
                return AjaxJson.getSuccessData(commentService.searchLikesById(commentLikes.getCommentId()));
            } else {
                return AjaxJson.getError("Fail to add a reply like");
            }
        }
    }

    @ApiOperation(value = "查阅未读的评论点赞", tags = "论坛-后台类")
    @GetMapping("/readOne")
    public AjaxJson readOne(@RequestParam int likeId) {
        CommentLikes commentLikes = commentLikesService.getById(likeId);
        if (commentLikes == null)
            return AjaxJson.getError("No such like");
        commentLikes.setReadCondition(1);
        commentLikesService.updateById(commentLikes);
        return AjaxJson.getSuccess("Success to check");
    }

    @ApiOperation(value = "通过id删除点赞信息", tags = "论坛-评论类")
    @GetMapping("/deleteById")
    public AjaxJson deleteByCommentLikesId(@RequestParam int Id) {
        System.out.println(commentLikesService.getClass().getName());
        return AjaxJson.getSuccessData(commentLikesService.deleteById(Id));
    }

    @ApiOperation(value = "通过评论id删除点赞信息", tags = "论坛-评论类")
    @GetMapping("/deleteByCommentId")
    public AjaxJson deleteByCommentId(@RequestParam int commentId) {
        System.out.println(commentLikesService.getClass().getName());
        return AjaxJson.getSuccessData(commentLikesService.deleteByCommentId(commentId));
    }

    @ApiOperation(value = "通过用户id删除点赞信息", tags = "论坛-评论类")
    @GetMapping("/deleteByUserId")
    public AjaxJson deleteByUserId(@RequestParam int userId) {
        System.out.println(commentLikesService.getClass().getName());
        return AjaxJson.getSuccessData(commentLikesService.deleteByUserId(userId));
    }
}
