package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.dto.RepliesDTO;
import com.mumu.dto.ReplyDTO;
import com.mumu.dto.ReplyLikesDTO;
import com.mumu.dto.ReplyWithLikesDTO;
import com.mumu.entity.Reply;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-23
 */
public interface ReplyService extends IService<Reply> {
    List<ReplyDTO> selectList();    // 获取全部信息（回复）

    ReplyDTO findById(int id);  // 通过回复id获取回复信息

    // 谁发的回复
    List<ReplyDTO> findByUserId(int userId);    // 通过用户id获取回复信息

    // 对谁的回复
    List<ReplyDTO> findByTargetId(int targetId);    // 通过对象id获取回复信息

    // 针对哪条评论的回复
    List<ReplyDTO> findByCommentId(int commentId);  // 通过评论id获取回复信息

    Reply findIfExitById(int Id);   // 通过回复id获取回复信息

    List<RepliesDTO> searchAllZero();    // 获取全部未审核的回复信息

    List<RepliesDTO> searchAllInValid(); // 获取全部未通过的回复信息

    int deleteById(int Id); // 通过回复id删除回复信息

    int deleteByUserId(int userId); // deleteByUserId

    int deleteByCommentId(int commentId);   // deleteByCommentId

    ReplyWithLikesDTO searchById(int Id);   // 通过id获取回复信息

    List<ReplyWithLikesDTO> searchByUserId(int userId); // 通过用户id获取回复信息（无论通过与否）

    List<ReplyWithLikesDTO> searchByTargetId(int targetId); // 通过目标id获取回复信息

    List<ReplyWithLikesDTO> searchByCommentId(int commentId);   // 通过评论id获取回复信息

    List<RepliesDTO> searchUnread(int targetId); // 获取全部未读的回复信息

    List<ReplyLikesDTO> searchAllUnreadLikes(int userId);   // 获取全部未读的回复点赞

    int searchLikesById(int id);    // 通过回复id获取点赞数

    List<Integer> searchByKeyWords(String keyWords);    // 关键词检索
}
