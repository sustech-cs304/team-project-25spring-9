package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Data
@ToString
public class CommentImgDTO {
    private String imgName;

    private ResponseEntity<byte[]> img;

    public CommentImgDTO(String imgName, ResponseEntity<byte[]> img) {
        this.imgName = imgName;
        this.img = img;
    }
}
