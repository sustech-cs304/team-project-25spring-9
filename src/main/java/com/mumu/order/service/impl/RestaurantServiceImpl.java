package com.mumu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.order.entity.DTO.FoodWindowDTO;
import com.mumu.order.entity.DTO.RestaurantDTO;
import com.mumu.order.entity.Food;
import com.mumu.order.entity.FoodWindow;
import com.mumu.order.entity.Restaurant;
import com.mumu.order.mapper.RestaurantMapper;
import com.mumu.order.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@Service
public class RestaurantServiceImpl extends ServiceImpl<RestaurantMapper, Restaurant> implements RestaurantService {
    @Autowired
    RestaurantMapper mapper;

    @Override
    public RestaurantDTO getAllFood(Restaurant restaurant) {
        MPJLambdaWrapper<Restaurant> lambdaWrapper = new MPJLambdaWrapper<Restaurant>()
                .selectAll(Restaurant.class)
                .selectCollection(FoodWindow.class, RestaurantDTO::getFoodWindowList, a -> a
                        .collection(Food.class, FoodWindowDTO::getFoodList))
                .leftJoin(FoodWindow.class, FoodWindow::getRestaurantId, Restaurant::getRestaurantId)
                .leftJoin(Food.class, Food::getWindowId, FoodWindow::getWindowId)
                .eq(restaurant.getRestaurantName() != null, Restaurant::getRestaurantName, restaurant.getRestaurantName())
                .eq(restaurant.getRestaurantId() != null, Restaurant::getRestaurantId, restaurant.getRestaurantId())
                .eq(FoodWindow::getWindowValid, true)
                .eq(Food::getFoodValid, true);
        MPJLambdaWrapper<Restaurant> lambdaWrapper1 = new MPJLambdaWrapper<Restaurant>()
                .selectAll(Restaurant.class)
                .selectCollection(FoodWindow.class, RestaurantDTO::getFoodWindowList)
                .leftJoin(FoodWindow.class, FoodWindow::getRestaurantId, Restaurant::getRestaurantId)
                .leftJoin(Food.class, Food::getWindowId, FoodWindow::getWindowId)
                .eq(restaurant.getRestaurantName() != null, Restaurant::getRestaurantName, restaurant.getRestaurantName())
                .eq(restaurant.getRestaurantId() != null, Restaurant::getRestaurantId, restaurant.getRestaurantId())
                .eq(FoodWindow::getWindowValid, true);
        RestaurantDTO r1 = mapper.selectJoinOne(RestaurantDTO.class, lambdaWrapper1);
        RestaurantDTO r2 = mapper.selectJoinOne(RestaurantDTO.class, lambdaWrapper);
        Map<Integer, FoodWindowDTO> m = new HashMap<>();
        for (FoodWindowDTO f : r2.getFoodWindowList()) {
            m.put(f.getWindowId(), f);
        }
        for (FoodWindowDTO f : r1.getFoodWindowList()) {
            if (m.get(f.getWindowId()) == null) {
                f.setFoodList(new ArrayList<>());
                r2.getFoodWindowList().add(f);
                m.put(f.getWindowId(), f);
            }
        }
        return r2;
    }

    @Override
    public RestaurantDTO getDeletedFood(Restaurant restaurant) {
        MPJLambdaWrapper<Restaurant> lambdaWrapper = new MPJLambdaWrapper<Restaurant>()
                .selectAll(Restaurant.class)
                .selectCollection(FoodWindow.class, RestaurantDTO::getFoodWindowList, a -> a
                        .collection(Food.class, FoodWindowDTO::getFoodList))
                .leftJoin(FoodWindow.class, FoodWindow::getRestaurantId, Restaurant::getRestaurantId)
                .leftJoin(Food.class, Food::getWindowId, FoodWindow::getWindowId)
                .eq(restaurant.getRestaurantName() != null, Restaurant::getRestaurantName, restaurant.getRestaurantName())
                .eq(restaurant.getRestaurantId() != null, Restaurant::getRestaurantId, restaurant.getRestaurantId())
                .eq(Food::getFoodValid, false);
        return mapper.selectJoinOne(RestaurantDTO.class, lambdaWrapper);
    }

    @Override
    public List<Restaurant> getAllRestaurant() {
        MPJLambdaWrapper<Restaurant> lambdaWrapper = new MPJLambdaWrapper<Restaurant>()
                .selectAll(Restaurant.class)
                .eq(Restaurant::getRestaurantValid, true);
        return mapper.selectJoinList(Restaurant.class, lambdaWrapper);
    }

    @Override
    public List<Restaurant> getDeletedRestaurant() {
        MPJLambdaWrapper<Restaurant> lambdaWrapper = new MPJLambdaWrapper<Restaurant>()
                .selectAll(Restaurant.class)
                .eq(Restaurant::getRestaurantValid, false);
        return mapper.selectJoinList(Restaurant.class, lambdaWrapper);
    }

    @Override
    public Boolean nameExist(String name) {
        return 0 != mapper.selectCount(
                new LambdaQueryWrapper<Restaurant>().eq(Restaurant::getRestaurantName, name));
    }

    @Override
    public Boolean idExist(Integer id) {
        return 0 != mapper.selectCount(new LambdaQueryWrapper<Restaurant>().eq(Restaurant::getRestaurantId, id));
    }

    @Override
    public Integer getRestaurantId(String name) {
        return mapper.selectOne(new LambdaQueryWrapper<Restaurant>().eq(Restaurant::getRestaurantName, name)).getRestaurantId();
    }

    @Override
    public Boolean deleteByRestaurantId(Integer restaurantId) {
        return mapper.update(null, new LambdaUpdateWrapper<Restaurant>()
                .eq(Restaurant::getRestaurantId, restaurantId)
                .set(Restaurant::getRestaurantValid, false)) > 0;
    }

    @Override
    public Boolean recoverByRestaurantId(Integer restaurantId) {
        return mapper.update(null, new LambdaUpdateWrapper<Restaurant>()
                .eq(Restaurant::getRestaurantId, restaurantId)
                .set(Restaurant::getRestaurantValid, true)) > 0;
    }
}
