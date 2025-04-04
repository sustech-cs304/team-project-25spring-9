package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.dto.*;
import com.mumu.entity.Comment;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-14
 */
public interface CommentService extends IService<Comment> {
    List<CommentDTO> selectList();  // 获取所有信息（评论、回复）

    CommentDTO findById(int Id);    // 通过评论id获取所有信息（评论、回复）

    // 谁发的评论
    List<CommentDTO> findByUserId(int userId);  // 通过用户id获取所有信息（评论、回复）

    // 对于哪栋建筑的评论
    List<CommentDTO> findByBuildingId(int buildingId);  // 通过建筑id获取所有信息（评论、回复）

    Comment findIfExitById(int Id); // 通过评论id获取评论信息

    List<CommentAdminDTO> searchAllUnchecked();  // 获取全部未审核的回复信息

    List<CommentAdminDTO> searchAllInValid();   // 获取全部未通过的回复信息

    int deleteById(int id); // 通过评论id删除评论

    int deleteByUserId(int id); // 通过用户id删除评论

    int deleteByBuildingId(int id); // 通过建筑id删除评论

    List<Comment> allCommentsOrderByLikes();    // 所有话题的id和内容（按照点赞数量排序）

    List<Comment> allCommentsOrderByReplies();  // 所有话题的id和内容（按照回复数量排序）

    Long countAllValid();   // 所有已通过评论数

    List<Comment> topCommentsOrderByLikes(int num); // 热度前十（按照点赞数量排序）

    List<Comment> topCommentsOrderByReplies(int num);   // 热度前十（按照回复数量排序）

    CommentsDTO oneComment(int Id); // 指定话题信息

    List<CommentUserDTO> searchCommentsByUserId(int userId);    // 用户id获取评论信息，无论通过与否

    List<CommentLikesDTO> searchAllUnreadLikes(int userId); // 获取全部未读的评论点赞

    List<LikesDTO> allUnreadLikes(int userId);  // 获取全部未读的点赞，评论和回复都有

    List<Integer> searchByKeyWords(String keyWords);    // 关键词检索

    int searchLikesById(int Id);    // 通过评论id获取点赞数

    int searchRepliesById(int Id);  // 通过评论id获取回复数

    Comment oneCommentById(int Id); // 通过评论id获取评论信息

    List<Integer> getIdByUserId(int userId);  // 通过用户id获取评论id

    List<Integer> getIdByBuildingId(int buildingId);  // 通过建筑id获取评论id
}
