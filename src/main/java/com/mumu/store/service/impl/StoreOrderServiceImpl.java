package com.mumu.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.entity.User;
import com.mumu.store.entity.Commodity;
import com.mumu.store.entity.DTO.OrderItemDTO;
import com.mumu.store.entity.DTO.StoreOrderDTO;
import com.mumu.store.entity.StoreOrder;
import com.mumu.store.entity.StoreOrderItem;
import com.mumu.store.mapper.StoreOrderMapper;
import com.mumu.store.service.StoreOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
@Service
public class StoreOrderServiceImpl extends ServiceImpl<StoreOrderMapper, StoreOrder> implements StoreOrderService {
    @Autowired
    StoreOrderMapper mapper;

    @Override
    public List<StoreOrderDTO> getUserOrder(User user) {
        MPJLambdaWrapper<StoreOrder> lambdaWrapper = new MPJLambdaWrapper<StoreOrder>()
                .selectAll(StoreOrder.class)
                .selectCollection(StoreOrderItem.class, StoreOrderDTO::getCommodityList, b -> b
                        .collection(Commodity.class, OrderItemDTO::getCommoditylist))
                .leftJoin(StoreOrderItem.class, StoreOrderItem::getOrderId, StoreOrder::getOrderId)
                .leftJoin(User.class, User::getUserId, StoreOrder::getUserId)
                .leftJoin(Commodity.class, Commodity::getCommodityId, StoreOrderItem::getItemId)
                .eq(User::getUserId, user.getUserId());
        return mapper.selectJoinList(StoreOrderDTO.class, lambdaWrapper);
    }

    @Override
    public StoreOrderDTO getUserOrder(Integer orderId) {
        MPJLambdaWrapper<StoreOrder> lambdaWrapper = new MPJLambdaWrapper<StoreOrder>()
                .selectAll(StoreOrder.class)
                .selectCollection(StoreOrderItem.class, StoreOrderDTO::getCommodityList, b -> b
                        .collection(Commodity.class, OrderItemDTO::getCommoditylist))
                .leftJoin(StoreOrderItem.class, StoreOrderItem::getOrderId, StoreOrder::getOrderId)
                .leftJoin(User.class, User::getUserId, StoreOrder::getUserId)
                .leftJoin(Commodity.class, Commodity::getCommodityId, StoreOrderItem::getItemId)
                .eq(StoreOrder::getOrderId, orderId);
        return mapper.selectJoinOne(StoreOrderDTO.class, lambdaWrapper);
    }

    @Override
    public String getPrice(Integer orderId) {
        return String.format("%.2f", mapper.selectOne(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getOrderId, orderId)).getPrice());
    }

    @Override
    public Boolean pay(StoreOrder o) {
        return 1 == mapper.updateById(o);
    }

    @Override
    public Boolean getPayState(Integer orderId) {
        return mapper.selectOne(new LambdaQueryWrapper<StoreOrder>().eq(StoreOrder::getOrderId, orderId)).getPayed();
    }

    @Override
    public Boolean deleteByOrderId(Integer orderId) {
        return mapper.deleteById(orderId) > 0;
    }
}
