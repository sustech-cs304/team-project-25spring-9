package com.mumu.order.entity.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Data
public class OrderDTO {
    private static final long serialVersionUID = 1L;

    private Integer orderId;
    private Integer userId;
    private LocalDateTime foodOrderTime;
    private LocalDateTime payTime;
    private LocalDateTime foodGetTime;
    private Boolean payed;
    private Boolean ordersValid;
    private Boolean orderTaken;
    private Double orderPrice;
    private ArrayList<FoodOrderDTO> foodOrders;
}
