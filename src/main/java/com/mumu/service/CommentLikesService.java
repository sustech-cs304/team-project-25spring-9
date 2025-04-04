package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.CommentLikes;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-05
 */
public interface CommentLikesService extends IService<CommentLikes> {
    CommentLikes searchIfExit(CommentLikes commentLikes);

    int deleteById(int id);

    int deleteByCommentId(int id);

    int deleteByUserId(int id);
}
