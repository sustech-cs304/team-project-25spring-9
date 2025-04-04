package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@ToString
public class ReplyLikesDTO {
    // reply
    private Integer replyId;

    private String replyContent;

    // reply_likes
    private Integer likeId;

    private LocalDateTime likeTime;

    private Integer readCondition;

    // user
    private UserInfoDTO userInformation;

    private ResponseEntity<byte[]> imgOfUser;
}
