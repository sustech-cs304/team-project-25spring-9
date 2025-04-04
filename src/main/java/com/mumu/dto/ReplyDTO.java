package com.mumu.dto;


import com.mumu.entity.User;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class ReplyDTO {
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

    // count from reply_likes
    private List<Integer> replyLiked;

    // userInformation: userId, userName, userPassword, userImg
    private User userInformation;

    // targetInformation: targetId, targetName, targetPassword, targetImg
    private User targetInformation;
}
