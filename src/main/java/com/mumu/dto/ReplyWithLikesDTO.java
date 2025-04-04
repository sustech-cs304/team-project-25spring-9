package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class ReplyWithLikesDTO {
    // reply
    private Integer replyId;

    private Integer userId;

    private Integer targetId;

    private Integer repliedId;

    private Integer commentId;

    private String replyContent;

    private LocalDateTime replyTime;

    private Integer replyLikes;

    private Integer replyValid;

    private Integer replyRead;

    // likes list for reply
    private List<Integer> replyLiked;
}
