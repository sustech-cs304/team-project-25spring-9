package com.mumu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.order.entity.FoodWindow;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-12-02
 */
public interface FoodWindowService extends IService<FoodWindow> {

    Boolean nameExist(String name);

    Boolean deleteByWindowId(Integer windowId);
}
