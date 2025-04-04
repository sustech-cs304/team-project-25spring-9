package com.mumu.dto;

import com.mumu.entity.Building;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class BuildingWithImgListDTO {
    // building
    private Building building;

    private List<BuildingImgDTO> imgOfBuilding;

    public BuildingWithImgListDTO(Building building, List<BuildingImgDTO> imgOfBuilding) {
        this.building = building;
        this.imgOfBuilding = imgOfBuilding;
    }
}
