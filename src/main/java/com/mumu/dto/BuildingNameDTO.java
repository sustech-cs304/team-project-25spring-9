package com.mumu.dto;

import lombok.Data;
import lombok.ToString;
import org.springframework.http.ResponseEntity;

@Data
@ToString
public class BuildingNameDTO {
    private Integer buildingId;

    private String buildingName;

    private ResponseEntity<byte[]> img;
}
