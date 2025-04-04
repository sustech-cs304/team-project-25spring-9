package com.mumu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Reply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "reply_id", type = IdType.AUTO)
    private Integer replyId;

    /**
     * 进行回复的用户
     */
    private Integer userId;

    /**
     * 被回复的用户
     */
    private Integer targetId;

    /**
     * -1代表是直接回复的评论
     */
    private Integer repliedId;

    /**
     * 被回复的评论
     */
    private Integer commentId;

    private String replyContent;

    private LocalDateTime replyTime;

    private Integer replyLikes;

    private Integer replyValid;

    private Integer replyRead;
}
