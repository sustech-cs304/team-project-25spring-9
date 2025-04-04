package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class BuildingCommentsDTO {
    // building
    private Integer buildingId;

    private String buildingName;

    private Integer buildingImg;

    private String buildingDescription;

    private Double buildingX;

    private Double buildingY;

    private Integer buildingComments;

    private List<BuildingImgDTO> imgOfBuilding;

    // CommentDTO
    private List<CommentsDTO> comments;
}
