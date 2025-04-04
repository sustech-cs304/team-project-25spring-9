package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class CommentDTO {
    // comment
    private Integer commentId;

    private Integer commentImg;

    private List<CommentImgDTO> imgOfComment;

    private Integer userId;

    private Integer buildingId;

    private String commentContent;

    private LocalDateTime commentTime;

    private Integer commentLikes;

    private Integer commentReplies;

    private Integer commentValid;

    // count from comment_likes
    private List<Integer> commentLiked;

    // User: userId, userName, userPassword, userImg
    private UserDTO userInformation;

    // reply: replyId, userId, targetId, targetIsComment, replyContent, replyLiked
    private List<ReplyDTO> replies;
}
