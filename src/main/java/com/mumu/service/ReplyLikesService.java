package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.ReplyLikes;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-05
 */
public interface ReplyLikesService extends IService<ReplyLikes> {
    ReplyLikes searchIfExit(ReplyLikes replyLikes);

    int deleteById(int id);

    int deleteByReplyId(int id);

    int deleteByUserId(int id);
}
