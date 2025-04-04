package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
public class CommentAdminDTO {
    // building
    private Integer buildingId;

    private String buildingName;

    // comment
    private Integer commentId;

    private Integer commentImg;

    private List<CommentImgDTO> imgOfComment;

    private String commentContent;

    private LocalDateTime commentTime;
    
    private Integer commentValid;

    // user
    private UserInfoDTO userInformation;

    private ResponseEntity<byte[]> imgOfUser;
}
