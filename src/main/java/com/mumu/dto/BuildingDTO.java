package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class BuildingDTO {
    // building
    private Integer buildingId;

    private String buildingName;

    private Integer buildingImg;

    private String buildingDescription;

    private Double buildingX;

    private Double buildingY;

    private Integer buildingComments;

    // CommentDTO
    private List<CommentDTO> comments;
}
