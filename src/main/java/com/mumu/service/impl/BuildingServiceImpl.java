package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.dto.*;
import com.mumu.entity.*;
import com.mumu.mapper.BuildingMapper;
import com.mumu.service.BuildingImgService;
import com.mumu.service.BuildingService;
import com.mumu.service.CommentService;
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
 * @since 2023-10-16
 */
@Service
public class BuildingServiceImpl extends ServiceImpl<BuildingMapper, Building> implements BuildingService {
    @Autowired
    BuildingMapper mapper;
    @Autowired
    CommentService commentService;
    @Autowired
    BuildingImgService buildingImgService;

    public MPJLambdaWrapper<Building> getBuilding() {
        return new MPJLambdaWrapper<Building>()
                .selectAll(Building.class)
                .selectCollection(Comment.class, BuildingDTO::getComments, commentDTOBuilder -> commentDTOBuilder
                        .collection(Reply.class, CommentDTO::getReplies, replyDTOBuilder -> replyDTOBuilder
                                .association("replyUser", User.class, ReplyDTO::getUserInformation)
                                .association("replyTarget", User.class, ReplyDTO::getTargetInformation)
                        )
                )
                .leftJoin(Comment.class, Comment::getBuildingId, Building::getBuildingId)
                .leftJoin(Reply.class, Reply::getCommentId, Comment::getCommentId)
                .leftJoin(User.class, "replyUser", User::getUserId, Reply::getUserId)
                .leftJoin(User.class, "replyTarget", User::getUserId, Reply::getTargetId);
    }

    @Override
    // 查询所有信息（建筑、评论、回复）
    public List<BuildingDTO> selectList() {
        MPJLambdaWrapper<Building> queryWrapper = getBuilding();
        return mapper.selectJoinList(BuildingDTO.class, queryWrapper);
    }

    @Override
    // 根据建筑id查询所有信息（建筑、评论、回复）
    public BuildingDTO findById(int id) {
        MPJLambdaWrapper<Building> queryWrapper = getBuilding().eq(Building::getBuildingId, id);
        return mapper.selectJoinOne(BuildingDTO.class, queryWrapper);
    }

    @Override
    // 通过名字获取建筑id
    public Integer getIdByName(String name) {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .eq(Building::getBuildingName, name);
        Building building = mapper.selectOne(queryWrapper);
        return building == null ? -1 : building.getBuildingId();
    }

    @Override
    // 通过坐标获取建筑名字
    public String getIdByLocation(double x, double y) {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .eq(Building::getBuildingX, x)
                .eq(Building::getBuildingY, y);
        Building building = mapper.selectOne(queryWrapper);
        return building == null ? null : building.getBuildingName();
    }

    @Override
    // 通过建筑id获取建筑信息
    public Building oneBuildingById(int id) {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                // 建筑信息
                .selectAll(Building.class)
                .eq(Building::getBuildingId, id);
        return mapper.selectOne(queryWrapper);
    }

    @Override
    // 通过建筑id删除所有相关信息（建筑、评论、回复）
    public int deleteById(int Id) {
        return commentService.deleteByBuildingId(Id) +
                mapper.deleteJoin(JoinWrappers.delete(Building.class)
                        .delete(Building.class)
                        .eq(Building::getBuildingId, Id));
    }

    @Override
    // 获取所有建筑的id和名称
    public List<BuildingInfoDTO> allBuildings() {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .select(Building::getBuildingId,
                        Building::getBuildingName);
        return mapper.selectJoinList(BuildingInfoDTO.class, queryWrapper);
    }

    @Override
    // 获取所有建筑的名称和第一张图片
    public List<BuildingNameDTO> allBuildingsFirst() {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .selectAs(Building::getBuildingId, BuildingNameDTO::getBuildingId)
                .selectAs(Building::getBuildingName, BuildingNameDTO::getBuildingName);
        return mapper.selectJoinList(BuildingNameDTO.class, queryWrapper);
    }

    @Override
    // 获取所有建筑的id，名称，坐标
    public List<BuildingLocationDTO> allBuildingsLocation() {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .select(Building::getBuildingId,
                        Building::getBuildingName,
                        Building::getBuildingX,
                        Building::getBuildingY);
        return mapper.selectJoinList(BuildingLocationDTO.class, queryWrapper);
    }

    @Override
    // 获取所有建筑的全部信息
    public List<Building> allBuildingsInfo() {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .selectAll(Building.class);
        return mapper.selectList(queryWrapper);
    }

    @Override
    // 获取所有建筑的全部信息以及最后一张图片
    public List<BuildingWithImgDTO> allBuildingsInfoWithImg() {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .selectAssociation(Building.class, BuildingWithImgDTO::getBuilding);
        return mapper.selectJoinList(BuildingWithImgDTO.class, queryWrapper);
    }

    @Override
    // 通过建筑id获取建筑信息以及有关话题
    public BuildingCommentsDTO commentsRelatedById(int id) {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                // 建筑信息
                .selectAll(Building.class)
                .eq(Building::getBuildingId, id)
                // 评论信息
                .leftJoin(Comment.class, Comment::getBuildingId, Building::getBuildingId)
                .leftJoin(User.class, "commentUser", User::getUserId, Comment::getUserId)
                .leftJoin(CommentLikes.class, CommentLikes::getCommentId, Comment::getCommentId)
                .and(commentConditions -> commentConditions
                        .isNull(Comment::getCommentId).or()
                        .eq(Comment::getCommentValid, 1).or()
                        .eq(Comment::getCommentContent, "初始评论")
                )
                .selectCollection(Comment.class, BuildingCommentsDTO::getComments, commentDTOBuilder -> commentDTOBuilder
                        .result(Comment::getCommentId)
                        .result(Comment::getCommentImg)
                        .result(Comment::getCommentContent)
                        .result(Comment::getCommentTime)
                        .result(Comment::getCommentLikes)
                        .result(Comment::getCommentReplies)
                        .result(Comment::getCommentValid)
                        // 用户信息
                        .association("commentUser", User.class, CommentsDTO::getUserInformation, userDTOBuilder -> userDTOBuilder
                                .result(User::getUserId)
                                .result(User::getUserImg)
                                .result(User::getUserName)
                                .result(User::getUserNickname)
                        )
                        // 点赞信息
                        .collection(CommentLikes.class, CommentsDTO::getLikes, commentLikesDTOBuilder -> commentLikesDTOBuilder
                                .result(CommentLikes::getUserId)
                        )
                        // 回复信息
                        .collection(Reply.class, CommentsDTO::getReplies, repliesDTOBuilder -> repliesDTOBuilder
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
                )
                .leftJoin(Reply.class, Reply::getCommentId, Comment::getCommentId)
                .leftJoin(User.class, "replyUser", User::getUserId, Reply::getUserId)
                .leftJoin(ReplyLikes.class, ReplyLikes::getReplyId, Reply::getReplyId)
                .and(replyConditions -> replyConditions
                        .isNull(Reply::getReplyId).or()
                        .eq(Reply::getReplyValid, 1).or()
                        .eq(Reply::getReplyContent, "初始回复")
                )
                .orderByDesc(Comment::getCommentTime);
        return mapper.selectJoinOne(BuildingCommentsDTO.class, queryWrapper);
    }

    @Override
    // 通过建筑名称获取建筑信息
    public Building oneBuildingByName(String name) {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                // 建筑信息
                .select(Building::getBuildingId,
                        Building::getBuildingName,
                        Building::getBuildingImg,
                        Building::getBuildingDescription,
                        Building::getBuildingComments)
                .eq(Building::getBuildingName, name);
        return mapper.selectOne(queryWrapper);
    }

    @Override
    // 热度前十（按照点赞数量排序）
    public List<Building> topBuildingsOrderByComments(int num) {
        MPJLambdaWrapper<Building> queryWrapper = new MPJLambdaWrapper<Building>()
                .selectAll(Building.class)
                .orderByDesc("building_comments")
                .last("limit " + num);
        return mapper.selectList(queryWrapper);
    }

    @Override
    // 关键词检索
    public List<Integer> searchByKeyWords(String keyWords) {
        String[] keyWordArray = keyWords.split("\\p{Punct}+");
        Stream<Integer> result = Stream.empty();
        for (String keyWord : keyWordArray) {
            Stream<Integer> add = mapper.selectJoinList(Integer.class, new MPJLambdaWrapper<Building>()
                    .select(Building::getBuildingId)
                    .like(Building::getBuildingName, keyWord)
                    .or()
                    .like(Building::getBuildingDescription, keyWord)
                    .orderByDesc("building_comments")).stream();
            result = Stream.concat(result, add);
        }
        return result.distinct().toList();
    }

    @Override
    // 关键词检索，只返回建筑名称匹配的建筑id
    public List<Integer> searchBuildingByKeyWords(String keyWords) {
        String[] keyWordArray = keyWords.split("\\p{Punct}+");
        Stream<Integer> result = Stream.empty();
        for (String keyWord : keyWordArray) {
            Stream<Integer> add = mapper.selectJoinList(Integer.class, new MPJLambdaWrapper<Building>()
                    .select(Building::getBuildingId)
                    .like(Building::getBuildingName, keyWord)
                    .orderByDesc("building_comments")).stream();
            result = Stream.concat(result, add);
        }
        return result.distinct().toList();
    }
}
