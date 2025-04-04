package com.mumu.order.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.order.entity.Food;
import com.mumu.order.service.FoodService;
import com.mumu.order.service.RestaurantService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-11-02
 */
@RestController
@RequestMapping("/food")
public class FoodController {
    @Autowired
    FoodService service;
    @Autowired
    RestaurantService restaurantService;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.foodPath}")
    private String foodImgPath;

    @ApiOperation(value = "获取食品图片", tags = "点餐类")
    @GetMapping("/img")
    public AjaxJson getFoodImg(@RequestParam Integer foodId) {
        return AjaxJson.getSuccessData(minioUtilS.download(String.format("%s.jpeg", foodId), foodImgPath));
    }

    @ApiOperation(value = "增加食品", tags = "食堂管理类")
    @SaCheckRole("admin")
    @PostMapping("/add")
    public AjaxJson addFood(@RequestParam MultipartFile file, @RequestParam String foodName,
                            @RequestParam Integer foodAmount, @RequestParam Integer windowId
            , @RequestParam Double foodPrice) {

        Food f = new Food();
        f.setWindowId(windowId);
        f.setFoodAmount(foodAmount);
        f.setFoodName(foodName);
        f.setFoodPrice(foodPrice);
        if (!service.save(f)) {
            throw new RuntimeException("fail to add restaurant");
        }
        minioUtilS.upload(file, foodImgPath, String.format("%s.jpeg", f.getFoodId()));
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "更改食品", tags = "食堂管理类")
    @SaCheckRole("admin")
    @PostMapping("/change")
    public AjaxJson changeFood(MultipartFile file, @RequestParam Integer foodId, String foodName,
                               Integer foodAmount, Integer windowId
            , @RequestParam Double foodPrice) {

        Food f = new Food();
        f.setFoodId(foodId);
        f.setWindowId(windowId);
        f.setFoodAmount(foodAmount);
        f.setFoodName(foodName);
        f.setFoodPrice(foodPrice);
        if (!service.updateById(f)) {
            throw new RuntimeException("fail to add restaurant");
        }
        if (file != null) {
            minioUtilS.upload(file, foodImgPath, String.format("%s.jpeg", f.getFoodId()));
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "删除食品", tags = "食堂管理类")
    @SaCheckRole("admin")
    @PostMapping("/delete")
    public AjaxJson deleteFood(@RequestParam Integer foodId) {
        if (!service.deleteByFoodId(foodId)) {
            throw new RuntimeException("fail to delete food");
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "恢复食品", tags = "食堂管理类")
    @SaCheckRole("admin")
    @PostMapping("/recover")
    public AjaxJson recoverFood(@RequestParam Integer foodId) {
        if (!service.recoverByFoodId(foodId)) {
            throw new RuntimeException("fail to delete food");
        }
        return AjaxJson.getSuccess();
    }

}

