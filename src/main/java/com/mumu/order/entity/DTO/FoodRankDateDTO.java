package com.mumu.order.entity.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class FoodRankDateDTO {
    private Integer foodRankId;

    private Integer foodId;

    private Integer foodCnt;

    private LocalDate date;
    private String foodName;
    private String windowName;
    private String restaurantName;
}

