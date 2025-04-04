package com.mumu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.order.entity.DTO.RestaurantDTO;
import com.mumu.order.entity.Restaurant;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
public interface RestaurantService extends IService<Restaurant> {

    RestaurantDTO getAllFood(Restaurant restaurant);

    RestaurantDTO getDeletedFood(Restaurant restaurant);

    List<Restaurant> getAllRestaurant();


    List<Restaurant> getDeletedRestaurant();

    Boolean nameExist(String name);


    Boolean idExist(Integer id);

    Integer getRestaurantId(String name);

    Boolean deleteByRestaurantId(Integer restaurantId);

    Boolean recoverByRestaurantId(Integer restaurantId);
}
