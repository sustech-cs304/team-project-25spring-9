package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class RepliesDTO {
    // reply
    private Integer replyId;

    private Integer repliedId;

    private Integer commentId;

    private String replyContent;

    private LocalDateTime replyTime;

    private Integer replyLikes;

    private Integer replyValid;

    private Integer replyRead;

    // user
    private UserInfoDTO userInformation;

    private ResponseEntity<byte[]> imgOfUser;

    // likes
    private List<Integer> likes;
}
