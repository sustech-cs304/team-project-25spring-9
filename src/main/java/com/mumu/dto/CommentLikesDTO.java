package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class CommentLikesDTO {
    // comment
    private Integer commentId;

    private Integer commentImg;

    private List<CommentImgDTO> imgOfComment;

    private String commentContent;

    // comment_likes
    private Integer likeId;

    private LocalDateTime likeTime;

    private Integer readCondition;

    // user
    private UserInfoDTO userInformation;

    private ResponseEntity<byte[]> imgOfUser;
}
