package com.mumu.order.entity.DTO;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class OrderUserDTO {
    private Integer userId;

    private String userName;

    private String userImg;

    private String userNickname;

    private List<OrderDTO> orderList;
}
