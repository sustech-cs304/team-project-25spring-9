package com.mumu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.entity.User;
import com.mumu.mapper.UserMapper;
import com.mumu.order.entity.DTO.FoodDTO;
import com.mumu.order.entity.DTO.FoodOrderDTO;
import com.mumu.order.entity.DTO.OrderDTO;
import com.mumu.order.entity.DTO.OrderUserDTO;
import com.mumu.order.entity.*;
import com.mumu.order.mapper.OrderMapper;
import com.mumu.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public OrderUserDTO getUserOrder(User user) {
        MPJLambdaWrapper<User> lambdaWrapper = new MPJLambdaWrapper<User>()
                .selectAll(User.class)//某个用户
                .selectCollection(Order.class, OrderUserDTO::getOrderList, b -> b//某个订单
                        .collection(FoodOrder.class, OrderDTO::getFoodOrders, c -> c
                                .collection(Food.class, FoodOrderDTO::getFood, d -> d
                                        .collection(Restaurant.class, FoodDTO::getRestaurant))))
                .leftJoin(Order.class, Order::getUserId, User::getUserId)
                .leftJoin(FoodOrder.class, FoodOrder::getOrderId, Order::getOrderId)
                .leftJoin(Food.class, Food::getFoodId, FoodOrder::getFoodId)
                .leftJoin(FoodWindow.class, FoodWindow::getWindowId, Food::getWindowId)
                .leftJoin(Restaurant.class, Restaurant::getRestaurantId, FoodWindow::getRestaurantId)
                .eq(user.getUserId() != null, User::getUserId, user.getUserId())
                .eq(user.getUserName() != null, User::getUserName, user.getUserName())

                .eq(Order::getOrdersValid, true);
        return userMapper.selectJoinOne(OrderUserDTO.class, lambdaWrapper);
    }

    @Override
    public OrderDTO getUserOrder(Integer orderId) {
        MPJLambdaWrapper<Order> lambdaWrapper = new MPJLambdaWrapper<Order>()
                .selectAll(Order.class)
                .selectCollection(FoodOrder.class, OrderDTO::getFoodOrders, b -> b
                        .collection(Food.class, FoodOrderDTO::getFood, d -> d
                                .collection(Restaurant.class, FoodDTO::getRestaurant)))
                .leftJoin(FoodOrder.class, FoodOrder::getOrderId, Order::getOrderId)
                .leftJoin(Food.class, Food::getFoodId, FoodOrder::getFoodId)
                .leftJoin(FoodWindow.class, FoodWindow::getWindowId, Food::getWindowId)
                .leftJoin(Restaurant.class, Restaurant::getRestaurantId, FoodWindow::getRestaurantId)

                .eq(Order::getOrderId, orderId);
        return mapper.selectJoinOne(OrderDTO.class, lambdaWrapper);
    }


    @Override
    public String getPrice(Integer orderId) {
        return String.format("%.2f", mapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderId, orderId)).getOrderPrice());
    }

    @Override
    public Integer getOrderId(Integer userId, LocalDateTime time) {
        return mapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getUserId, userId).eq(Order::getFoodOrderTime, time.format((DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))).getOrderId();
    }

    @Override
    public Boolean pay(Order o) {
        return 1 == mapper.updateById(o);
    }

    @Override
    public Boolean getPayState(Integer orderId) {
        return mapper.selectOne(new LambdaQueryWrapper<Order>().eq(Order::getOrderId, orderId)).getPayed();
    }

    @Override
    public Boolean deleteByOrderId(Integer orderId) {
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Order::getOrderId, orderId)
                .set(Order::getOrdersValid, false);
        return mapper.update(null, updateWrapper) > 0;
    }

    @Override
    public Boolean getOrder(Integer orderId) {
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Order::getOrderId, orderId)
                .set(Order::getOrderTaken, true);
        return mapper.update(null, updateWrapper) > 0;
    }

    @Override
    public Boolean recoverByOrderId(Integer orderId) {
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Order::getOrderId, orderId)
                .set(Order::getOrdersValid, true);
        return mapper.update(null, updateWrapper) > 0;
    }

    @Override
    public OrderUserDTO getUserDeletedOrder(User user) {
        MPJLambdaWrapper<User> lambdaWrapper = new MPJLambdaWrapper<User>()
                .selectAll(User.class)//某个用户
                .selectCollection(Order.class, OrderUserDTO::getOrderList, b -> b//某个订单
                        .collection(FoodOrder.class, OrderDTO::getFoodOrders, c -> c
                                .collection(Food.class, FoodOrderDTO::getFood)))
                .leftJoin(Order.class, Order::getUserId, User::getUserId)
                .leftJoin(FoodOrder.class, FoodOrder::getOrderId, Order::getOrderId)
                .leftJoin(Food.class, Food::getFoodId, FoodOrder::getFoodId)
                .eq(user.getUserId() != null, User::getUserId, user.getUserId())
                .eq(user.getUserName() != null, User::getUserName, user.getUserName())
                .eq(Order::getOrdersValid, false);
        return userMapper.selectJoinOne(OrderUserDTO.class, lambdaWrapper);
    }
}
