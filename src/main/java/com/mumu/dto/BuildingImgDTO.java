package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Data
@ToString
public class BuildingImgDTO {
    private String imgName;

    private ResponseEntity<byte[]> img;

    public BuildingImgDTO(String imgName, ResponseEntity<byte[]> img) {
        this.imgName = imgName;
        this.img = img;
    }
}
