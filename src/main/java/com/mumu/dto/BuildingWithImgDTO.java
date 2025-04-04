package com.mumu.dto;

import com.mumu.entity.Building;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Data
@ToString
public class BuildingWithImgDTO {
    // building
    private Building building;

    private ResponseEntity<byte[]> img;
}
