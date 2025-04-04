package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.DeleteJoinWrapper;
import com.mumu.entity.ReplyLikes;
import com.mumu.mapper.ReplyLikesMapper;
import com.mumu.service.ReplyLikesService;
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
public class ReplyLikesServiceImpl extends ServiceImpl<ReplyLikesMapper, ReplyLikes> implements ReplyLikesService {
    @Autowired
    ReplyLikesMapper mapper;

    @Override
    public ReplyLikes searchIfExit(ReplyLikes replyLikes) {
        MPJQueryWrapper<ReplyLikes> queryWrapper = new MPJQueryWrapper<ReplyLikes>()
                .selectAll(ReplyLikes.class)
                .eq("user_id", replyLikes.getUserId())
                .eq("reply_id", replyLikes.getReplyId());
        return mapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteById(int id) {
        DeleteJoinWrapper<ReplyLikes> wrapper = JoinWrappers.delete(ReplyLikes.class)
                .delete(ReplyLikes.class)
                .eq(ReplyLikes::getId, id);
        return mapper.deleteJoin(wrapper);
    }

    @Override
    public int deleteByReplyId(int replyId) {
        DeleteJoinWrapper<ReplyLikes> wrapper = JoinWrappers.delete(ReplyLikes.class)
                .delete(ReplyLikes.class)
                .eq(ReplyLikes::getReplyId, replyId);
        return mapper.deleteJoin(wrapper);
    }

    @Override
    public int deleteByUserId(int userId) {
        DeleteJoinWrapper<ReplyLikes> wrapper = JoinWrappers.delete(ReplyLikes.class)
                .delete(ReplyLikes.class)
                .eq(ReplyLikes::getUserId, userId);
        return mapper.deleteJoin(wrapper);
    }
}
