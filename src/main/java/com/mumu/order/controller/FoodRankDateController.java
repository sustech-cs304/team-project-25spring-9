package com.mumu.order.controller;


import com.mumu.order.service.FoodRankDateService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-12-02
 */
@RestController
@RequestMapping("/food-rank-date")
public class FoodRankDateController {
    @Autowired
    FoodRankDateService service;

    @ApiOperation(value = "获取食品排名", tags = "数据类")
    @GetMapping("/all")
    public AjaxJson getFoodRank(Date date, Integer windowId, Integer restaurantId) {
        return AjaxJson.getSuccessData(service.getfoodRank(date, windowId, restaurantId));
    }

}

