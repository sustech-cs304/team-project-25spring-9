package com.mumu.store.entity.DTO;

import com.mumu.store.entity.Commodity;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemDTO {
    private Integer storeOrderItemId;

    private Integer orderId;

    private Integer itemId;

    private Integer itemCount;

    private String itemName;

    private Double itemPrice;

    private List<Commodity> commoditylist;

}
