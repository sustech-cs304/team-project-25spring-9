package com.mumu.store.controller;


import com.mumu.store.service.StoreOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-12-03
 */
@RestController
@RequestMapping("/store-order-item")
public class StoreOrderItemController {
    @Autowired
    StoreOrderItemService service;
}

