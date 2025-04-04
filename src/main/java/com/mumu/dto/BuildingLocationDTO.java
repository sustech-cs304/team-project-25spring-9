package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BuildingLocationDTO {
    private int buildingId;

    private String buildingName;

    private Double buildingX;

    private Double buildingY;
}
