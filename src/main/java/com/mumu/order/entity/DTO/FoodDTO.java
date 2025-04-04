package com.mumu.order.entity.DTO;

import com.mumu.order.entity.Restaurant;
import lombok.Data;

import java.util.List;

@Data
public class FoodDTO {
    private Integer foodId;

    private String foodName;

    private String foodImg;

    private List<Restaurant> restaurant;
    /**
     * the amount of food that can be ordered daily
     */
    private Integer foodAmount;

    /**
     * the amount of food that have be ordered today
     */
    private Integer foodOrderedAmount;

    private Integer windowId;
    /**
     * the amount of food that have be ordered up to now
     */
    private Integer foodTotalOrderedAmount;

    private Double foodPrice;

    private Boolean foodValid;
}
