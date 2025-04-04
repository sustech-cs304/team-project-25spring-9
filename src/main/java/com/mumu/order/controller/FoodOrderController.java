package com.mumu.order.controller;


import com.mumu.order.service.FoodOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@RestController
@RequestMapping("/food-order")
public class FoodOrderController {
    @Autowired
    FoodOrderService service;
}

