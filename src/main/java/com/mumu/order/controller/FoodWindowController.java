package com.mumu.order.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.order.entity.FoodWindow;
import com.mumu.order.service.FoodWindowService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-12-02
 */
@RestController
@RequestMapping("/food-window")
public class FoodWindowController {
    @Autowired
    FoodWindowService service;

    @ApiOperation(value = "增加窗口", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/add")
    public AjaxJson addWindow(@RequestParam String windowName, @RequestParam Integer restaurantId) {
        FoodWindow w = new FoodWindow();
        w.setWindowName(windowName);
        w.setRestaurantId(restaurantId);

        if (!service.save(w)) {
            throw new RuntimeException("fail to add window");
        }
        return AjaxJson.getSuccess(String.valueOf(w.getWindowId()));
    }

    @ApiOperation(value = "删除窗口", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/delete")
    public AjaxJson deleteRestaurant(@RequestParam Integer windowId) {
        if (!service.deleteByWindowId(windowId)) {
            throw new RuntimeException("fail to delete restaurant");
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "重命名窗口", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/rename")
    public AjaxJson renameRestaurant(@RequestParam Integer windowId, @RequestParam String windowName) {
        FoodWindow r = new FoodWindow();
        r.setWindowId(windowId);
        r.setWindowName(windowName);
        service.updateById(r);
        return AjaxJson.getSuccess();
    }
}

