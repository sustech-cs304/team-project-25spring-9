package com.mumu.order.entity.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RestaurantRankDateDTO {
    private LocalDate date;
    private String restaurantName;
    private Integer cnt;
}

