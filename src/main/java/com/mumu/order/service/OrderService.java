package com.mumu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.User;
import com.mumu.order.entity.DTO.OrderDTO;
import com.mumu.order.entity.DTO.OrderUserDTO;
import com.mumu.order.entity.Order;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
public interface OrderService extends IService<Order> {

    OrderUserDTO getUserOrder(User user);

    OrderDTO getUserOrder(Integer orderId);

    String getPrice(Integer orderId);

    Integer getOrderId(Integer userId, LocalDateTime time);

    Boolean pay(Order o);

    Boolean getPayState(Integer orderId);

    Boolean deleteByOrderId(Integer orderId);

    Boolean getOrder(Integer orderId);

    Boolean recoverByOrderId(Integer orderId);

    OrderUserDTO getUserDeletedOrder(User user);
}
