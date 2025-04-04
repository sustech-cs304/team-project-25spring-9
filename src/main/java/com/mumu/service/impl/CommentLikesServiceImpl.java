package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.DeleteJoinWrapper;
import com.mumu.entity.CommentLikes;
import com.mumu.mapper.CommentLikesMapper;
import com.mumu.service.CommentLikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-11-05
 */
@Service
public class CommentLikesServiceImpl extends ServiceImpl<CommentLikesMapper, CommentLikes> implements CommentLikesService {
    @Autowired
    CommentLikesMapper mapper;

    @Override
    public CommentLikes searchIfExit(CommentLikes commentLikes) {
        MPJQueryWrapper<CommentLikes> queryWrapper = new MPJQueryWrapper<CommentLikes>()
                .selectAll(CommentLikes.class)
                .eq("user_id", commentLikes.getUserId())
                .eq("comment_id", commentLikes.getCommentId());
        return mapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteById(int id) {
        DeleteJoinWrapper<CommentLikes> wrapper = JoinWrappers.delete(CommentLikes.class)
                .delete(CommentLikes.class)
                .eq(CommentLikes::getId, id);
        return mapper.deleteJoin(wrapper);
    }

    @Override
    public int deleteByCommentId(int commentId) {
        DeleteJoinWrapper<CommentLikes> wrapper = JoinWrappers.delete(CommentLikes.class)
                .delete(CommentLikes.class)
                .eq(CommentLikes::getCommentId, commentId);
        return mapper.deleteJoin(wrapper);
    }

    @Override
    public int deleteByUserId(int userId) {
        DeleteJoinWrapper<CommentLikes> wrapper = JoinWrappers.delete(CommentLikes.class)
                .delete(CommentLikes.class)
                .eq(CommentLikes::getUserId, userId);
        return mapper.deleteJoin(wrapper);
    }
}
