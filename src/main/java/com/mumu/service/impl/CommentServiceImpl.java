package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.dto.*;
import com.mumu.entity.*;
import com.mumu.mapper.CommentMapper;
import com.mumu.mapper.ReplyMapper;
import com.mumu.service.CommentImgService;
import com.mumu.service.CommentLikesService;
import com.mumu.service.CommentService;
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
 * @since 2023-10-14
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    CommentMapper mapper;
    @Autowired
    ReplyMapper replyMapper;
    @Autowired
    ReplyService replyService;
    @Autowired
    CommentImgService commentImgService;
    @Autowired
    CommentLikesService commentLikesService;

    public MPJLambdaWrapper<Comment> getComment() {
        return new MPJLambdaWrapper<Comment>()
                .selectAll(Comment.class)
                // commentUser
                .selectAssociation("commentUser", User.class, CommentDTO::getUserInformation, commentDTOBuilder -> commentDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserName)
                        .result(User::getUserImg)
                        .result(User::getUserNickname))
                .leftJoin(User.class, "commentUser", User::getUserId, Comment::getUserId)
                // likes
                .selectCollection(CommentLikes.class, CommentDTO::getCommentLiked, commentDTOBuilder -> commentDTOBuilder
                        .result(CommentLikes::getUserId)
                )
                .leftJoin(CommentLikes.class, CommentLikes::getCommentId, Comment::getCommentId)
                // replies
                .selectCollection(Reply.class, CommentDTO::getReplies, replyDTOBuilder -> replyDTOBuilder
                        .association("replyUser", User.class, ReplyDTO::getUserInformation)
                        .association("replyTarget", User.class, ReplyDTO::getTargetInformation)
                        .collection(ReplyLikes.class, ReplyDTO::getReplyLiked, commentDTOBuilder -> commentDTOBuilder
                                .result(ReplyLikes::getUserId)
                        )
                )
                .leftJoin(Reply.class, Reply::getCommentId, Comment::getCommentId)
                .leftJoin(User.class, "replyUser", User::getUserId, Reply::getUserId)
                .leftJoin(User.class, "replyTarget", User::getUserId, Reply::getTargetId)
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .orderByDesc("comment_time");
    }

    @Override
    // 获取所有信息（评论、回复）
    public List<CommentDTO> selectList() {
        MPJLambdaWrapper<Comment> queryWrapper = getComment();
        return mapper.selectJoinList(CommentDTO.class, queryWrapper);
    }

    @Override
    // 通过评论id获取所有信息（评论、回复）
    public CommentDTO findById(int Id) {
        MPJLambdaWrapper<Comment> queryWrapper = getComment().eq(Comment::getCommentId, Id);
        return mapper.selectJoinOne(CommentDTO.class, queryWrapper);
    }

    @Override
    // 通过用户id获取所有信息（评论、回复）
    public List<CommentDTO> findByUserId(int userId) {
        MPJLambdaWrapper<Comment> queryWrapper = getComment().eq(Comment::getUserId, userId);
        return mapper.selectJoinList(CommentDTO.class, queryWrapper);
    }

    @Override
    // 通过建筑id获取所有信息（评论、回复）
    public List<CommentDTO> findByBuildingId(int buildingId) {
        MPJLambdaWrapper<Comment> queryWrapper = getComment().eq(Comment::getBuildingId, buildingId);
        return mapper.selectJoinList(CommentDTO.class, queryWrapper);
    }

    @Override
    // 通过评论id获取评论信息
    public Comment findIfExitById(int Id) {
        MPJQueryWrapper<Comment> queryWrapper = new MPJQueryWrapper<Comment>()
                .selectAll(Comment.class)
                .eq("comment_id", Id);
        return mapper.selectOne(queryWrapper);
    }

    @Override
    // 获取全部未审核的回复信息
    public List<CommentAdminDTO> searchAllUnchecked() {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                // building
                .selectAs(Building::getBuildingId, CommentAdminDTO::getBuildingId)
                .selectAs(Building::getBuildingName, CommentAdminDTO::getBuildingName)
                .leftJoin(Building.class, Building::getBuildingId, Comment::getBuildingId)
                // comment
                .selectAs(Comment::getCommentId, CommentAdminDTO::getCommentId)
                .selectAs(Comment::getCommentImg, CommentAdminDTO::getCommentImg)
                .selectAs(Comment::getCommentContent, CommentAdminDTO::getCommentContent)
                .selectAs(Comment::getCommentTime, CommentAdminDTO::getCommentTime)
                .selectAs(Comment::getCommentValid, CommentAdminDTO::getCommentValid)
                .eq("comment_valid", 0)
                .ne("comment_content", "初始评论")
                // user
                .selectAssociation("commentUser", User.class, CommentAdminDTO::getUserInformation, commentDTOBuilder -> commentDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserName)
                        .result(User::getUserImg)
                        .result(User::getUserNickname))
                .leftJoin(User.class, "commentUser", User::getUserId, Comment::getUserId)
                .orderByDesc("comment_time");
        return mapper.selectJoinList(CommentAdminDTO.class, queryWrapper);
    }

    @Override
    // 获取全部未通过的回复信息
    public List<CommentAdminDTO> searchAllInValid() {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                // building
                .selectAs(Building::getBuildingId, CommentAdminDTO::getBuildingId)
                .selectAs(Building::getBuildingName, CommentAdminDTO::getBuildingName)
                .leftJoin(Building.class, Building::getBuildingId, Comment::getBuildingId)
                // comment
                .selectAs(Comment::getCommentId, CommentAdminDTO::getCommentId)
                .selectAs(Comment::getCommentImg, CommentAdminDTO::getCommentImg)
                .selectAs(Comment::getCommentContent, CommentAdminDTO::getCommentContent)
                .selectAs(Comment::getCommentTime, CommentAdminDTO::getCommentTime)
                .selectAs(Comment::getCommentValid, CommentAdminDTO::getCommentValid)
                .eq("comment_valid", -1)
                // user
                .selectAssociation("commentUser", User.class, CommentAdminDTO::getUserInformation, commentDTOBuilder -> commentDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserName)
                        .result(User::getUserImg)
                        .result(User::getUserNickname))
                .leftJoin(User.class, "commentUser", User::getUserId, Comment::getUserId)
                .orderByDesc("comment_time");
        return mapper.selectJoinList(CommentAdminDTO.class, queryWrapper);
    }

    @Override
    // 通过评论id删除评论
    public int deleteById(int Id) {
        return commentLikesService.deleteByCommentId(Id) +
                replyService.deleteByCommentId(Id) +
                mapper.deleteJoin(JoinWrappers.delete(Comment.class)
                        .delete(Comment.class)
                        .eq(Comment::getCommentId, Id));
    }

    @Override
    // 通过用户id删除评论
    public int deleteByUserId(int userId) {
        int result = 0;
        List<Integer> commentList = mapper.selectJoinList(Integer.class,
                new MPJLambdaWrapper<Comment>()
                        .select(Comment::getCommentId)
                        .eq("user_id", userId));
        for (int id : commentList) {
            result += deleteById(id);
        }
        return result;
    }

    @Override
    // 通过建筑id删除评论
    public int deleteByBuildingId(int buildingId) {
        int result = 0;
        List<Integer> commentList = mapper.selectJoinList(Integer.class,
                new MPJLambdaWrapper<Comment>()
                        .select(Comment::getCommentId)
                        .eq("building_id", buildingId));
        for (int id : commentList) {
            result += deleteById(id);
        }
        return result;
    }

    @Override
    // 所有话题的id和名称（按照点赞数量排序）
    public List<Comment> allCommentsOrderByLikes() {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .selectAll(Comment.class)
                .eq(Comment::getCommentValid, 1)
                .orderByDesc("comment_likes", "comment_time");
        return mapper.selectList(queryWrapper);
    }

    @Override
    // 所有话题的id和内容（按照回复数量排序）
    public List<Comment> allCommentsOrderByReplies() {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .selectAll(Comment.class)
                .eq(Comment::getCommentValid, 1)
                .orderByDesc("comment_replies", "comment_time");
        return mapper.selectList(queryWrapper);
    }

    @Override
    // 所有已通过评论数
    public Long countAllValid() {
        MPJQueryWrapper<Comment> queryWrapper = new MPJQueryWrapper<Comment>()
                .select("comment_id")
                .orderByDesc("comment_time")
                .eq("comment_valid", 1);
        return mapper.selectJoinCount(queryWrapper);
    }

    @Override
    // 热度前十（按照评论点赞排序）
    public List<Comment> topCommentsOrderByLikes(int num) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .selectAll(Comment.class)
                .eq(Comment::getCommentValid, 1)
                .orderByDesc("comment_likes", "comment_time")
                .last("limit " + num);
        return mapper.selectList(queryWrapper);
    }

    @Override
    // 热度前十（按照评论数量排序）
    public List<Comment> topCommentsOrderByReplies(int num) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .selectAll(Comment.class)
                .eq(Comment::getCommentValid, 1)
                .orderByDesc("comment_replies", "comment_time")
                .last("limit " + num);
        return mapper.selectList(queryWrapper);
    }

    @Override
    // 指定话题信息
    public CommentsDTO oneComment(int Id) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                // 评论信息
                .selectAll(Comment.class)
                .eq(Comment::getCommentId, Id)
                .eq(Comment::getCommentValid, 1)
                // 用户信息
                .selectAssociation("commentUser", User.class, CommentsDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                // 点赞信息
                .selectCollection(CommentLikes.class, CommentsDTO::getLikes, commentLikesDTOBuilder -> commentLikesDTOBuilder
                        .result(CommentLikes::getUserId)
                )
                // 回复信息
                .selectCollection(Reply.class, CommentsDTO::getReplies, repliesDTOBuilder -> repliesDTOBuilder
                        .result(Reply::getReplyId)
                        .result(Reply::getRepliedId)
                        .result(Reply::getCommentId)
                        .result(Reply::getReplyContent)
                        .result(Reply::getReplyTime)
                        .result(Reply::getReplyLikes)
                        .result(Reply::getReplyValid)
                        .result(Reply::getReplyRead)
                        // 用户信息
                        .association("replyUser", User.class, RepliesDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                                .result(User::getUserId)
                                .result(User::getUserImg)
                                .result(User::getUserName)
                                .result(User::getUserNickname)
                        )
                        // 点赞信息
                        .collection(ReplyLikes.class, RepliesDTO::getLikes, replyLikesDTOBuilder -> replyLikesDTOBuilder
                                .result(ReplyLikes::getUserId)
                        )
                )
                .leftJoin(User.class, "commentUser", User::getUserId, Comment::getUserId)
                .leftJoin(CommentLikes.class, CommentLikes::getCommentId, Comment::getCommentId)
                .leftJoin(Reply.class, Reply::getCommentId, Comment::getCommentId)
                .leftJoin(User.class, "replyUser", User::getUserId, Reply::getUserId)
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .and(replyConditions -> replyConditions
                        .isNull(Reply::getReplyId).or()
                        .eq(Reply::getReplyValid, 1).or()
                        .eq(Reply::getReplyContent, "初始回复")
                )
                .orderByDesc("comment_time");
        return mapper.selectJoinOne(CommentsDTO.class, queryWrapper);
    }

    @Override
    // 通过用户id获取评论信息，无论通过与否
    public List<CommentUserDTO> searchCommentsByUserId(int userId) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                // 建筑信息
                .selectAs(Building::getBuildingName, CommentUserDTO::getBuildingName)
                .leftJoin(Building.class, Building::getBuildingId, Comment::getBuildingId)
                // 评论信息
                .selectAs(Comment::getCommentId, CommentUserDTO::getCommentId)
                .selectAs(Comment::getCommentImg, CommentUserDTO::getCommentImg)
                .selectAs(Comment::getCommentContent, CommentUserDTO::getCommentContent)
                .selectAs(Comment::getCommentTime, CommentUserDTO::getCommentTime)
                .selectAs(Comment::getCommentValid, CommentUserDTO::getCommentValid)
                .eq(Comment::getUserId, userId)
                // 用户信息
                .selectAssociation(User.class, CommentUserDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, User::getUserId, Comment::getUserId);
        return mapper.selectJoinList(CommentUserDTO.class, queryWrapper);
    }

    @Override
    // 获取全部未读的评论点赞
    public List<CommentLikesDTO> searchAllUnreadLikes(int userId) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                // comment
                .selectAs(Comment::getCommentId, CommentLikesDTO::getCommentId)
                .selectAs(Comment::getCommentImg, CommentLikesDTO::getCommentImg)
                .selectAs(Comment::getCommentContent, CommentLikesDTO::getCommentContent)
                .eq(Comment::getCommentValid, 1)
                .eq(Comment::getUserId, userId)
                // likes
                .selectAs(CommentLikes::getId, CommentLikesDTO::getLikeId)
                .selectAs(CommentLikes::getLikeTime, CommentLikesDTO::getLikeTime)
                .selectAs(CommentLikes::getReadCondition, CommentLikesDTO::getReadCondition)
                .leftJoin(CommentLikes.class, CommentLikes::getCommentId, Comment::getCommentId)
                .eq("read_condition", 0)
                // user
                .selectAssociation(User.class, CommentLikesDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, User::getUserId, CommentLikes::getUserId)
                .orderByDesc("like_time", "comment_time");
        return mapper.selectJoinList(CommentLikesDTO.class, queryWrapper);
    }

    @Override
    // 获取全部未读的点赞，评论和回复都有
    public List<LikesDTO> allUnreadLikes(int userId) {
        MPJLambdaWrapper<Comment> commentWrapper = new MPJLambdaWrapper<Comment>()
                // comment
                .selectAs(Comment::getCommentId, LikesDTO::getCommentId)
                .selectAs("-1", LikesDTO::getReplyId)
                .selectAs(Comment::getCommentContent, LikesDTO::getContent)
                .eq(Comment::getCommentValid, 1)
                .eq(Comment::getUserId, userId)
                // likes
                .selectAs(CommentLikes::getId, LikesDTO::getLikeId)
                .selectAs(CommentLikes::getLikeTime, LikesDTO::getLikeTime)
                .leftJoin(CommentLikes.class, CommentLikes::getCommentId, Comment::getCommentId)
                .eq("read_condition", 0)
                // user
                .selectAssociation(User.class, LikesDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, User::getUserId, CommentLikes::getUserId);
        MPJLambdaWrapper<Reply> replyWrapper = new MPJLambdaWrapper<Reply>()
                // reply
                .selectAs(ReplyLikes::getReadCondition, LikesDTO::getCommentId)
                .selectAs(Reply::getReplyId, LikesDTO::getReplyId)
                .selectAs(Reply::getReplyContent, LikesDTO::getContent)
                .eq(Reply::getReplyValid, 1)
                .eq(Reply::getUserId, userId)
                // likes
                .selectAs(ReplyLikes::getId, LikesDTO::getLikeId)
                .selectAs(ReplyLikes::getLikeTime, LikesDTO::getLikeTime)
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .eq(ReplyLikes::getReadCondition, 0)
                // user
                .selectAssociation("reply", User.class, LikesDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                        .result(User::getUserId)
                        .result(User::getUserImg)
                        .result(User::getUserName)
                        .result(User::getUserNickname)
                )
                .leftJoin(User.class, "reply", User::getUserId, ReplyLikes::getUserId);
        List<LikesDTO> commentLikesDTOList = mapper.selectJoinList(LikesDTO.class, commentWrapper);
        List<LikesDTO> replyLikesDTOList = replyMapper.selectJoinList(LikesDTO.class, replyWrapper);
        return Stream.concat(commentLikesDTOList.stream(), replyLikesDTOList.stream()).toList();
    }

    @Override
    // 关键词检索
    public List<Integer> searchByKeyWords(String keyWords) {
        String[] keyWordArray = keyWords.split("\\p{Punct}+");
        Stream<Integer> result = Stream.empty();
        for (String keyWord : keyWordArray) {
            Stream<Integer> add = mapper.selectJoinList(Integer.class, new MPJLambdaWrapper<Comment>()
                    .select(Comment::getCommentId)
                    .like(Comment::getCommentContent, keyWord)
                    .eq(Comment::getCommentValid, 1)
                    .orderByDesc("comment_likes")).stream();
            result = Stream.concat(result, add);
        }
        return result.distinct().toList();
    }

    @Override
    // 通过评论id获取点赞数
    public int searchLikesById(int Id) {
        MPJQueryWrapper<Comment> queryWrapper = new MPJQueryWrapper<Comment>()
                .select("comment_likes")
                .eq("comment_id", Id)
                .eq("comment_valid", 1);
        Comment comment = mapper.selectOne(queryWrapper);
        return comment == null ? 0 : comment.getCommentLikes();
    }

    @Override
    // 通过评论id获取回复数
    public int searchRepliesById(int Id) {
        MPJQueryWrapper<Comment> queryWrapper = new MPJQueryWrapper<Comment>()
                .select("comment_replies")
                .eq("comment_id", Id)
                .eq("comment_valid", 1);
        Comment comment = mapper.selectOne(queryWrapper);
        return comment == null ? 0 : comment.getCommentReplies();
    }

    @Override
    // 通过评论id获取评论信息
    public Comment oneCommentById(int Id) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .selectAll(Comment.class)
                .eq(Comment::getCommentId, Id);
        return mapper.selectOne(queryWrapper);
    }

    @Override
    // 通过用户id获取评论id
    public List<Integer> getIdByUserId(int userId) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .select(Comment::getCommentId)
                .eq(Comment::getUserId, userId);
        return mapper.selectJoinList(Integer.class, queryWrapper);
    }

    @Override
    // 通过建筑id获取评论id
    public List<Integer> getIdByBuildingId(int buildingId) {
        MPJLambdaWrapper<Comment> queryWrapper = new MPJLambdaWrapper<Comment>()
                .select(Comment::getCommentId)
                .eq(Comment::getBuildingId, buildingId)
                .orderByDesc("comment_time");
        return mapper.selectJoinList(Integer.class, queryWrapper);
    }
}
