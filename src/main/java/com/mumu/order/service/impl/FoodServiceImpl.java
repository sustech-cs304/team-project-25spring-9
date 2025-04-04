package com.mumu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mumu.order.entity.Food;
import com.mumu.order.mapper.FoodMapper;
import com.mumu.order.service.FoodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@Service
public class FoodServiceImpl extends ServiceImpl<FoodMapper, Food> implements FoodService {
    @Autowired
    FoodMapper mapper;

    @Override
    public Integer getFoodId(String name) {
        return mapper.selectOne(new LambdaQueryWrapper<Food>().eq(Food::getFoodName, name)).getFoodId();
    }

    @Override
    public Boolean deleteByFoodId(Integer FoodId) {
        return mapper.update(null, new LambdaUpdateWrapper<Food>()
                .eq(Food::getFoodId, FoodId)
                .set(Food::getFoodValid, false)) > 0;
    }

    @Override
    public Boolean recoverByFoodId(Integer FoodId) {
        return mapper.update(null, new LambdaUpdateWrapper<Food>()
                .eq(Food::getFoodId, FoodId)
                .set(Food::getFoodValid, true)) > 0;
    }

}
