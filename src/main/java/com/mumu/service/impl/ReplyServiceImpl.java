package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.dto.RepliesDTO;
import com.mumu.dto.ReplyDTO;
import com.mumu.dto.ReplyLikesDTO;
import com.mumu.dto.ReplyWithLikesDTO;
import com.mumu.entity.Reply;
import com.mumu.entity.ReplyLikes;
import com.mumu.entity.User;
import com.mumu.mapper.ReplyMapper;
import com.mumu.service.ReplyLikesService;
import com.mumu.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-10-23
 */
@Service
public class ReplyServiceImpl extends ServiceImpl<ReplyMapper, Reply> implements ReplyService {
    @Autowired
    ReplyMapper mapper;
    @Autowired
    ReplyLikesService replyLikesService;

    public MPJLambdaWrapper<Reply> getReply() {
        return new MPJLambdaWrapper<Reply>()
                .selectAll(Reply.class)
                // user
                .selectAssociation("user", User.class, ReplyDTO::getUserInformation)
                .leftJoin(User.class, "user", User::getUserId, Reply::getUserId)
                // target
                .selectAssociation("target", User.class, ReplyDTO::getTargetInformation)
                .leftJoin(User.class, "target", User::getUserId, Reply::getTargetId)
                // likes
                .selectCollection(ReplyLikes.class, ReplyDTO::getReplyLiked, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .orderByDesc("reply_time");
    }

    @Override
    // 获取全部信息（回复）
    public List<ReplyDTO> selectList() {
        MPJLambdaWrapper<Reply> queryWrapper = getReply();
        return mapper.selectJoinList(ReplyDTO.class, queryWrapper);
    }

    @Override
    // 通过回复id获取回复信息
    public ReplyDTO findById(int Id) {
        MPJLambdaWrapper<Reply> queryWrapper = getReply().eq(Reply::getReplyId, Id);
        return mapper.selectJoinOne(ReplyDTO.class, queryWrapper);
    }

    @Override
    // 通过用户id获取回复信息
    public List<ReplyDTO> findByUserId(int userId) {
        MPJLambdaWrapper<Reply> queryWrapper = getReply().eq(Reply::getUserId, userId);
        return mapper.selectJoinList(ReplyDTO.class, queryWrapper);
    }

    @Override
    // 通过对象id获取回复信息
    public List<ReplyDTO> findByTargetId(int targetId) {
        MPJLambdaWrapper<Reply> queryWrapper = getReply().eq(Reply::getTargetId, targetId);
        return mapper.selectJoinList(ReplyDTO.class, queryWrapper);
    }

    @Override
    // 通过评论id获取回复信息
    public List<ReplyDTO> findByCommentId(int commentId) {
        MPJLambdaWrapper<Reply> queryWrapper = getReply().eq(Reply::getCommentId, commentId);
        return mapper.selectJoinList(ReplyDTO.class, queryWrapper);
    }

    @Override
    // 通过回复id获取回复信息
    public Reply findIfExitById(int Id) {
        MPJQueryWrapper<Reply> queryWrapper = new MPJQueryWrapper<Reply>()
                .selectAll(Reply.class)
                .eq("reply_id", Id);
        return mapper.selectOne(queryWrapper);
    }

    @Override
    // 获取全部未审核的回复信息
    public List<RepliesDTO> searchAllZero() {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                // reply
                .selectAs(Reply::getReplyId, RepliesDTO::getReplyId)
                .selectAs(Reply::getRepliedId, RepliesDTO::getRepliedId)
                .selectAs(Reply::getCommentId, RepliesDTO::getCommentId)
                .selectAs(Reply::getReplyContent, RepliesDTO::getReplyContent)
                .selectAs(Reply::getReplyTime, RepliesDTO::getReplyTime)
                .selectAs(Reply::getReplyLikes, RepliesDTO::getReplyLikes)
                .selectAs(Reply::getReplyValid, RepliesDTO::getReplyValid)
                .selectAs(Reply::getReplyRead, RepliesDTO::getReplyRead)
                .orderByDesc("reply_time")
                .eq("reply_valid", 0)
                .ne(Reply::getReplyContent, "初始回复")
                // user
                .selectAssociation(User.class, RepliesDTO::getUserInformation, replyDTOBuilder -> replyDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, "user", User::getUserId, Reply::getUserId)
                // likes
                .selectCollection(ReplyLikes.class, RepliesDTO::getLikes, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId);
        return mapper.selectJoinList(RepliesDTO.class, queryWrapper);
    }

    @Override
    // 获取全部未通过的回复信息
    public List<RepliesDTO> searchAllInValid() {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                // reply
                .selectAs(Reply::getReplyId, RepliesDTO::getReplyId)
                .selectAs(Reply::getRepliedId, RepliesDTO::getRepliedId)
                .selectAs(Reply::getCommentId, RepliesDTO::getCommentId)
                .selectAs(Reply::getReplyContent, RepliesDTO::getReplyContent)
                .selectAs(Reply::getReplyTime, RepliesDTO::getReplyTime)
                .selectAs(Reply::getReplyLikes, RepliesDTO::getReplyLikes)
                .selectAs(Reply::getReplyValid, RepliesDTO::getReplyValid)
                .selectAs(Reply::getReplyRead, RepliesDTO::getReplyRead)
                .orderByDesc("reply_time")
                .eq("reply_valid", -1)
                // user
                .selectAssociation(User.class, RepliesDTO::getUserInformation, replyDTOBuilder -> replyDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, "user", User::getUserId, Reply::getUserId)
                // likes
                .selectCollection(ReplyLikes.class, RepliesDTO::getLikes, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId);
        return mapper.selectJoinList(RepliesDTO.class, queryWrapper);
    }

    @Override
    // 通过回复id删除回复信息
    public int deleteById(int Id) {
        return replyLikesService.deleteByReplyId(Id) +
                mapper.deleteJoin(JoinWrappers.delete(Reply.class)
                        .delete(Reply.class)
                        .eq(Reply::getReplyId, Id));
    }

    @Override
    // deleteByUserId
    public int deleteByUserId(int userId) {
        int result = 0;
        List<Integer> replyList = mapper.selectJoinList(Integer.class,
                new MPJLambdaWrapper<Reply>()
                        .select(Reply::getReplyId)
                        .eq("user_id", userId));
        for (int id : replyList) {
            result += deleteById(id);
        }
        return result;
    }

    @Override
    // deleteByCommentId
    public int deleteByCommentId(int commentId) {
        int result = 0;
        List<Integer> replyList = mapper.selectJoinList(Integer.class,
                new MPJLambdaWrapper<Reply>()
                        .select(Reply::getReplyId)
                        .eq("comment_id", commentId));
        for (int id : replyList) {
            result += deleteById(id);
        }
        return result;
    }

    @Override
    // 通过id获取回复信息
    public ReplyWithLikesDTO searchById(int Id) {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                .selectAll(Reply.class)
                .selectCollection(ReplyLikes.class, ReplyWithLikesDTO::getReplyLiked, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .eq(Reply::getReplyId, Id)
                .eq(Reply::getReplyValid, 1);
        return mapper.selectJoinOne(ReplyWithLikesDTO.class, queryWrapper);
    }

    @Override
    // 通过用户id获取回复信息（无论通过与否）
    public List<ReplyWithLikesDTO> searchByUserId(int userId) {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                .selectAll(Reply.class)
                .selectCollection(ReplyLikes.class, ReplyWithLikesDTO::getReplyLiked, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .orderByDesc("reply_time")
                .eq(Reply::getUserId, userId);
        return mapper.selectJoinList(ReplyWithLikesDTO.class, queryWrapper);
    }

    @Override
    // 通过目标id获取回复信息
    public List<ReplyWithLikesDTO> searchByTargetId(int targetId) {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                .selectAll(Reply.class)
                .selectCollection(ReplyLikes.class, ReplyWithLikesDTO::getReplyLiked, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .orderByDesc("reply_time")
                .eq(Reply::getTargetId, targetId)
                .eq(Reply::getReplyValid, 1);
        return mapper.selectJoinList(ReplyWithLikesDTO.class, queryWrapper);
    }

    @Override
    // 通过评论id获取回复信息
    public List<ReplyWithLikesDTO> searchByCommentId(int commentId) {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                .selectAll(Reply.class)
                .selectCollection(ReplyLikes.class, ReplyWithLikesDTO::getReplyLiked, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .orderByDesc("reply_time")
                .eq(Reply::getCommentId, commentId)
                .eq(Reply::getReplyValid, 1);
        return mapper.selectJoinList(ReplyWithLikesDTO.class, queryWrapper);
    }

    @Override
    // 获取全部未读的回复信息
    public List<RepliesDTO> searchUnread(int targetId) {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                // reply
                .selectAs(Reply::getReplyId, RepliesDTO::getReplyId)
                .selectAs(Reply::getRepliedId, RepliesDTO::getRepliedId)
                .selectAs(Reply::getCommentId, RepliesDTO::getCommentId)
                .selectAs(Reply::getReplyContent, RepliesDTO::getReplyContent)
                .selectAs(Reply::getReplyTime, RepliesDTO::getReplyTime)
                .selectAs(Reply::getReplyLikes, RepliesDTO::getReplyLikes)
                .selectAs(Reply::getReplyValid, RepliesDTO::getReplyValid)
                .selectAs(Reply::getReplyRead, RepliesDTO::getReplyRead)
                // user
                .selectAssociation(User.class, RepliesDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserName)
                        .result(User::getUserImg)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, User::getUserId, Reply::getUserId)
                // likes
                .selectCollection(ReplyLikes.class, RepliesDTO::getLikes, replyDTOBuilder -> replyDTOBuilder
                        .result(ReplyLikes::getUserId)
                )
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .orderByDesc("reply_time")
                .eq("target_id", targetId)
                .eq("reply_valid", 1)
                .eq("reply_read", 0);
        return mapper.selectJoinList(RepliesDTO.class, queryWrapper);
    }

    @Override
    // 获取全部未读的回复点赞
    public List<ReplyLikesDTO> searchAllUnreadLikes(int userId) {
        MPJLambdaWrapper<Reply> queryWrapper = new MPJLambdaWrapper<Reply>()
                // comment
                .selectAs(Reply::getReplyId, ReplyLikesDTO::getReplyId)
                .selectAs(Reply::getReplyContent, ReplyLikesDTO::getReplyContent)
                .eq(Reply::getUserId, userId)
                // likes
                .selectAs(ReplyLikes::getId, ReplyLikesDTO::getLikeId)
                .selectAs(ReplyLikes::getLikeTime, ReplyLikesDTO::getLikeTime)
                .selectAs(ReplyLikes::getReadCondition, ReplyLikesDTO::getReadCondition)
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .eq("read_condition", 0)
                // user
                .selectAssociation(User.class, ReplyLikesDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, User::getUserId, ReplyLikes::getUserId)
                .orderByDesc("like_time", "reply_time");
        return mapper.selectJoinList(ReplyLikesDTO.class, queryWrapper);
    }

    @Override
    // 通过回复id获取点赞数
    public int searchLikesById(int Id) {
        MPJQueryWrapper<Reply> queryWrapper = new MPJQueryWrapper<Reply>()
                .select("reply_likes")
                .eq("reply_valid", 1)
                .eq("reply_id", Id);
        Reply reply = mapper.selectOne(queryWrapper);
        return reply == null ? 0 : reply.getReplyLikes();
    }

    @Override
    // 关键词检索
    public List<Integer> searchByKeyWords(String keyWords) {
        String[] keyWordArray = keyWords.split("\\p{Punct}+");
        Stream<Integer> result = Stream.empty();
        for (String keyWord : keyWordArray) {
            Stream<Integer> add = mapper.selectJoinList(Integer.class, new MPJLambdaWrapper<Reply>()
                    .select(Reply::getReplyId)
                    .like(Reply::getReplyContent, keyWord)

                    .eq(Reply::getReplyValid, 1)
                    .orderByDesc("reply_likes")).stream();
            result = Stream.concat(result, add);
        }
        return result.distinct().toList();
    }
}
