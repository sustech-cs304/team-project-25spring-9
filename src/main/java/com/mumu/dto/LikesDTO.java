package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@ToString
public class LikesDTO {
    // message
    private Integer commentId;

    private Integer replyId;

    private String content;

    // like info
    private Integer likeId;

    private LocalDateTime likeTime;

    // user
    private UserInfoDTO userInformation;

    private ResponseEntity<byte[]> imgOfUser;
}
