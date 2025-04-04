package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.CommentImg;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
public interface CommentImgService extends IService<CommentImg> {
    String firstImgIdGroupByCommentId(int commentId); // 获取评论第一张图片的名字

    String largestImgIdGroupByCommentId(int commentId);   // 获取评论最大的图片的名字

    List<String> allCommentImg(int commentId);    // 通过id获取评论的所有图片

    String deleteOneImg(int commentId, String imgName);    // 删除一张评论图片

    String deleteImgByCommentId(int commentId);   // 删除所有评论图片
}
