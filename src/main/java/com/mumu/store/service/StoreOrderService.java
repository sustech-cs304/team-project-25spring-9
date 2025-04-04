package com.mumu.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.User;
import com.mumu.store.entity.DTO.StoreOrderDTO;
import com.mumu.store.entity.StoreOrder;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
public interface StoreOrderService extends IService<StoreOrder> {

    List<StoreOrderDTO> getUserOrder(User user);

    StoreOrderDTO getUserOrder(Integer orderId);

    String getPrice(Integer orderId);

    Boolean pay(StoreOrder o);

    Boolean getPayState(Integer orderId);

    Boolean deleteByOrderId(Integer orderId);
}
