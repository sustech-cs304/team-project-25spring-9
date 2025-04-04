package com.mumu.order.service;

import com.mumu.order.entity.Food;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
public interface FoodService extends IService<Food> {

    Integer getFoodId(String name);

    Boolean deleteByFoodId(Integer FoodId);

    Boolean recoverByFoodId(Integer FoodId);
}
