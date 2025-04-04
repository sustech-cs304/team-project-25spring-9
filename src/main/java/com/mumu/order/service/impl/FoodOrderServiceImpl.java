package com.mumu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mumu.order.entity.FoodOrder;
import com.mumu.order.mapper.FoodOrderMapper;
import com.mumu.order.service.FoodOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@Service
public class FoodOrderServiceImpl extends ServiceImpl<FoodOrderMapper, FoodOrder> implements FoodOrderService {
    @Autowired
    FoodOrderMapper mapper;

    @Override
    public Boolean delete(Integer orderId) {
        LambdaQueryWrapper<FoodOrder> lambdaQueryWrapper = new LambdaQueryWrapper<FoodOrder>()
                .eq(FoodOrder::getOrderId, orderId);
        mapper.delete(lambdaQueryWrapper);
        return true;
    }

    @Override
    public Boolean deleteByOrderId(Integer orderId) {
        LambdaUpdateWrapper<FoodOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(FoodOrder::getOrderId, orderId)
                .set(FoodOrder::getOrderValid, false);
        return mapper.update(null, updateWrapper) > 0;
    }

}
