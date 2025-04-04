package com.mumu.order.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.order.entity.Restaurant;
import com.mumu.order.service.RestaurantService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * @since 2023-11-02
 */
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {
    @Autowired
    RestaurantService service;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.restaurantPath}")
    private String restaurantImgPath;

    @ApiOperation(value = "显示当前食堂全部菜品", tags = "点餐类")
    @GetMapping("/food/list")
    public AjaxJson getAllFood(@RequestParam Integer restaurantId) {
        Restaurant r = new Restaurant();
        r.setRestaurantId(restaurantId);
        return AjaxJson.getSuccessData(service.getAllFood(r));
    }

    @ApiOperation(value = "显示当前全部食堂", tags = "点餐类")
    @GetMapping("/list")
    public AjaxJson getAllRestaurant() {
        return AjaxJson.getSuccessData(service.getAllRestaurant());
    }

    @ApiOperation(value = "获取食堂图片", tags = "点餐类")
    @GetMapping("/img")
    public AjaxJson getRestaurantImg(@RequestParam Integer restaurantId) {
        return AjaxJson.getSuccessData(minioUtilS.download(String.format("%s.jpeg", restaurantId), restaurantImgPath));
    }

    @ApiOperation(value = "增加食堂", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/add")
    public AjaxJson addRestaurant(@RequestParam String restaurantName) {
        if (service.nameExist(restaurantName)) {
            throw new RuntimeException("name already exist!");
        }
        Restaurant r = new Restaurant();
        r.setRestaurantName(restaurantName);
        if (!service.save(r)) {
            throw new RuntimeException("fail to add restaurant");
        }
        return AjaxJson.getSuccess(String.valueOf(r.getRestaurantId()));
    }

    @ApiOperation(value = "删除食堂", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/delete")
    public AjaxJson deleteRestaurant(@RequestParam Integer restaurantId) {
        if (!service.deleteByRestaurantId(restaurantId)) {
            throw new RuntimeException("fail to delete restaurant");
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "重命名食堂", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/rename")
    public AjaxJson renameRestaurant(@RequestParam Integer restaurantId, @RequestParam String restaurantName) {
        Restaurant r = new Restaurant();
        r.setRestaurantId(restaurantId);
        r.setRestaurantName(restaurantName);
        service.updateById(r);
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "恢复食堂", tags = "食堂管理类")
    @SaCheckRole("admin")
    @GetMapping("/recover")
    public AjaxJson recoverRestaurant(@RequestParam Integer restaurantId) {
        if (!service.recoverByRestaurantId(restaurantId)) {
            throw new RuntimeException("fail to delete restaurant");
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "食堂回收站", tags = "食堂管理类")
    @GetMapping("/recycle")
    public AjaxJson getDeletedRestaurant() {
        return AjaxJson.getSuccessData(service.getDeletedRestaurant());
    }

    @ApiOperation(value = "食物回收站", tags = "食堂管理类")
    @GetMapping("/food/recycle")
    public AjaxJson getDeletedFood(@RequestParam Integer restaurantId) {
        Restaurant r = new Restaurant();
        r.setRestaurantId(restaurantId);
        return AjaxJson.getSuccessData(service.getDeletedFood(r));
    }

}

