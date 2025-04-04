package com.mumu.controller;


import com.mumu.entity.ReplyLikes;
import com.mumu.service.ReplyLikesService;
import com.mumu.service.ReplyService;
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
@RequestMapping("/replyLikes")
public class ReplyLikesController {
    @Autowired
    private ReplyLikesService replyLikesService;
    @Autowired
    ReplyService replyService;

    @ApiOperation(value = "添加点赞信息", tags = "论坛-回复类")
    @GetMapping("/addOne")
    public AjaxJson addOne(@RequestParam int userId, @RequestParam int replyId) {
        ReplyLikes replyLikes = new ReplyLikes();
        replyLikes.setUserId(userId);
        replyLikes.setReplyId(replyId);
        ReplyLikes temp = replyLikesService.searchIfExit(replyLikes);
        if (temp != null) {
            deleteByReplyId(replyId);
            return AjaxJson.getSuccessData(replyService.searchLikesById(temp.getReplyId()));
        } else {
            if (replyLikesService.saveOrUpdate(replyLikes)) {
                return AjaxJson.getSuccessData(replyService.searchLikesById(replyLikes.getReplyId()));
            } else {
                return AjaxJson.getError("Fail to add a reply like");
            }
        }
    }

    @ApiOperation(value = "查阅未读的回复点赞", tags = "论坛-后台类")
    @GetMapping("/readOne")
    public AjaxJson readOne(@RequestParam int likeId) {
        ReplyLikes replyLikes = replyLikesService.getById(likeId);
        if (replyLikes == null)
            return AjaxJson.getError("No such like");
        replyLikes.setReadCondition(1);
        replyLikesService.updateById(replyLikes);
        return AjaxJson.getSuccess("Success to check");
    }

    @ApiOperation(value = "通过id删除点赞信息", tags = "论坛-回复类")
    @GetMapping("/deleteById")
    public AjaxJson deleteByReplyLikesId(@RequestParam int Id) {
        System.out.println(replyLikesService.getClass().getName());
        return AjaxJson.getSuccessData(replyLikesService.deleteById(Id));
    }

    @ApiOperation(value = "通过回复id删除点赞信息", tags = "论坛-回复类")
    @GetMapping("/deleteReplyById")
    public AjaxJson deleteByReplyId(@RequestParam int replyId) {
        System.out.println(replyLikesService.getClass().getName());
        return AjaxJson.getSuccessData(replyLikesService.deleteByReplyId(replyId));
    }

    @ApiOperation(value = "通过用户id删除点赞信息", tags = "论坛-回复类")
    @GetMapping("/deleteByUserId")
    public AjaxJson deleteByUserId(@RequestParam int userId) {
        System.out.println(replyLikesService.getClass().getName());
        return AjaxJson.getSuccessData(replyLikesService.deleteByUserId(userId));
    }
}
