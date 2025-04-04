package com.mumu.order.entity.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class FoodOrderDTO {
    private static final long serialVersionUID = 1L;

    private Integer foodOrderId;

    private List<FoodDTO> food;

    private Integer orderId;

    private Integer foodAmount;

    private LocalDateTime foodGetTime;
}
