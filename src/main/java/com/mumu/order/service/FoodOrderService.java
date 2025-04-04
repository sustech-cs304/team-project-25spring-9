package com.mumu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.order.entity.FoodOrder;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
public interface FoodOrderService extends IService<FoodOrder> {

    Boolean delete(Integer orderId);

    Boolean deleteByOrderId(Integer orderId);
}
