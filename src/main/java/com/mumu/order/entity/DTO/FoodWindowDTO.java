package com.mumu.order.entity.DTO;

import com.mumu.order.entity.Food;
import lombok.Data;

import java.util.List;

@Data
public class FoodWindowDTO {
    private String windowName;
    private Integer windowId;
    private List<Food> foodList;
}
