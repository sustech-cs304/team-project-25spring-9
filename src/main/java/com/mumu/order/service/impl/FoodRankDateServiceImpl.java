package com.mumu.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.order.entity.DTO.FoodRankDateDTO;
import com.mumu.order.entity.Food;
import com.mumu.order.entity.FoodRankDate;
import com.mumu.order.entity.FoodWindow;
import com.mumu.order.entity.Restaurant;
import com.mumu.order.mapper.FoodRankDateMapper;
import com.mumu.order.service.FoodRankDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-12-02
 */
@Service
public class FoodRankDateServiceImpl extends ServiceImpl<FoodRankDateMapper, FoodRankDate> implements FoodRankDateService {
    @Autowired
    FoodRankDateMapper mapper;

    @Override
    public List<FoodRankDateDTO> getfoodRank(Date date, Integer windowId, Integer restaurantId) {
        MPJLambdaWrapper<FoodRankDate> lambdaWrapper = new MPJLambdaWrapper<FoodRankDate>()
                .selectAll(FoodRankDate.class)
                .selectAs(Restaurant::getRestaurantName, FoodRankDateDTO::getRestaurantName)
                .selectAs(FoodWindow::getWindowName, FoodRankDateDTO::getWindowName)
                .selectAs(Food::getFoodName, FoodRankDateDTO::getFoodName)
                .leftJoin(Food.class, Food::getFoodId, FoodRankDate::getFoodId)
                .leftJoin(FoodWindow.class, FoodWindow::getWindowId, Food::getWindowId)
                .leftJoin(Restaurant.class, Restaurant::getRestaurantId, FoodWindow::getRestaurantId)
                .eq(date != null, FoodRankDate::getDate, date)
                .eq(windowId != null, FoodWindow::getWindowId, windowId)
                .eq(restaurantId != null, Restaurant::getRestaurantId, restaurantId)
                .orderByDesc(FoodRankDate::getFoodCnt);
        return mapper.selectJoinList(FoodRankDateDTO.class, lambdaWrapper);
    }

//    @Override
//    public List<RestaurantRankDateDTO> getRestaurantDTO() {
//        MPJLambdaWrapper<FoodRankDate>lambdaWrapper=new MPJLambdaWrapper<FoodRankDate>()
//                .selectAs(FoodRankDate::getDate,RestaurantRankDateDTO::getDate)
//                .selectAs(Restaurant::getRestaurantName,RestaurantRankDateDTO::getRestaurantName)
//                .selectCount()
//
//    }
}







