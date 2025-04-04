package com.mumu.store.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.store.entity.StoreWindow;
import com.mumu.store.service.StoreWindowService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.RedisLockService;
import com.mumu.utils.RedisOptService;
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
 * @since 2023-12-03
 */
@RestController
@RequestMapping("/store-window")
public class StoreWindowController {
    @Autowired
    StoreWindowService service;
    @Autowired
    RedisOptService redisOptService;
    @Autowired
    RedisLockService redisLockService;

    @ApiOperation(value = "显示当前分类全部商品", tags = "商店类")
    @GetMapping("/list")
    public AjaxJson getAllItem() {
        return AjaxJson.getSuccessData(service.getAllItem());
    }

    @ApiOperation(value = "显示当前全部分类", tags = "商店类")
    @GetMapping("/window/list")
    public AjaxJson getAllWindow() {
        return AjaxJson.getSuccessData(service.getAllWindow());
    }

    @ApiOperation(value = "增加分类", tags = "商店管理类")
    @SaCheckRole("admin")
    @GetMapping("/add")
    public AjaxJson addwindow(@RequestParam String windowName) {
        StoreWindow sw = new StoreWindow();
        sw.setWindowName(windowName);
        if (!service.save(sw)) {
            throw new RuntimeException("fail to add window");
        }
        return AjaxJson.getSuccess(String.valueOf(sw.getWindowId()));
    }

    @ApiOperation(value = "删除分类", tags = "商店管理类")
    @SaCheckRole("admin")
    @GetMapping("/delete")
    public AjaxJson deleteRestaurant(@RequestParam Integer windowId) {

        return service.deleteWindow(windowId) ? AjaxJson.getSuccess() : AjaxJson.getError();
    }

    @ApiOperation(value = "重命名分类", tags = "商店管理类")
    @SaCheckRole("admin")
    @GetMapping("/rename")
    public AjaxJson renameRestaurant(@RequestParam Integer windowId, @RequestParam String windowName) {
        StoreWindow r = new StoreWindow();
        r.setWindowId(windowId);
        r.setWindowName(windowName);
        service.updateById(r);
        return AjaxJson.getSuccess();
    }
}

