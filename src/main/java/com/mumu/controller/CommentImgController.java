package com.mumu.controller;


import com.mumu.entity.Comment;
import com.mumu.service.CommentImgService;
import com.mumu.service.CommentService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
@RestController
@RequestMapping("/commentImg")
public class CommentImgController {
    @Autowired
    private CommentImgService commentImgService;
    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "通过id获取评论的所有图片", tags = "论坛-评论类")
    @GetMapping("/searchAllImg")
    public AjaxJson allCommentsImg(@RequestParam int commentId) {
        //Fot testing the concrete class
        System.out.println(commentImgService.getClass().getName());
        return AjaxJson.getSuccessData(commentImgService.allCommentImg(commentId));
    }

    @ApiOperation(value = "删除一张评论图片", tags = "论坛-评论类")
    @GetMapping("/deleteImg")
    public AjaxJson deleteImg(@RequestParam int commentId, @RequestParam String imgName) {
        System.out.println(commentImgService.getClass().getName());
        // check
        Comment comment = commentService.oneCommentById(commentId);
        if (comment == null)
            return AjaxJson.getError("Fail to find the comment");
        String result = commentImgService.deleteOneImg(commentId, imgName);
        if (!result.equals("删除文件成功"))
            return AjaxJson.getError("删除文件失败");
        else
            return AjaxJson.getSuccessData(comment.getCommentImg() - 1);
    }

    @ApiOperation(value = "删除所有评论图片", tags = "论坛-评论类")
    @GetMapping("/deleteAllImg")
    public AjaxJson deleteAllImg(@RequestParam int commentId) {
        System.out.println(commentImgService.getClass().getName());
        // check
        Comment comment = commentService.oneCommentById(commentId);
        if (comment == null)
            return AjaxJson.getError("Fail to find the comment");
        String result = commentImgService.deleteImgByCommentId(commentId);
        if (!result.equals("删除文件成功"))
            return AjaxJson.getError("删除文件失败");
        else
            return AjaxJson.getSuccess("删除文件成功");
    }
}
