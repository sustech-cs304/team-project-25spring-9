package com.mumu.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mumu.order.entity.FoodWindow;
import com.mumu.order.mapper.FoodWindowMapper;
import com.mumu.order.service.FoodWindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-12-02
 */
@Service
public class FoodWindowServiceImpl extends ServiceImpl<FoodWindowMapper, FoodWindow> implements FoodWindowService {
    @Autowired
    FoodWindowMapper mapper;

    @Override
    public Boolean nameExist(String name) {
        return 0 != mapper.selectCount(
                new LambdaQueryWrapper<FoodWindow>().eq(FoodWindow::getWindowName, name));
    }

    @Override
    public Boolean deleteByWindowId(Integer windowId) {
        return mapper.update(null, new LambdaUpdateWrapper<FoodWindow>()
                .eq(FoodWindow::getWindowId, windowId)
                .set(FoodWindow::getWindowValid, false)) > 0;
    }
}
