package com.mumu.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.order.entity.DTO.FoodRankDateDTO;
import com.mumu.order.entity.FoodRankDate;

import java.sql.Date;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-12-02
 */
public interface FoodRankDateService extends IService<FoodRankDate> {

    List<FoodRankDateDTO> getfoodRank(Date date, Integer windowId, Integer restaurantId);
}
