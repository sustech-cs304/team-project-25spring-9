package com.mumu.store.entity.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class StoreOrderDTO {
    private Integer orderId;

    private Integer userId;
    private String userName;

    private Double price;

    private LocalDateTime orderTime;

    private LocalDateTime payTime;

    private Boolean payed;

    private List<OrderItemDTO> commodityList;
}
