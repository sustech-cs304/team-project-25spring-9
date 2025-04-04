package com.mumu.order.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class RestaurantDTO {
    private String restaurantName;
    private Integer restaurantId;
    private List<FoodWindowDTO> foodWindowList;
}
