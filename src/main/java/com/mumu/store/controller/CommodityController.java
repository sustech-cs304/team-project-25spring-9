package com.mumu.store.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.store.entity.Commodity;
import com.mumu.store.service.CommodityService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import com.mumu.utils.RedisLockService;
import com.mumu.utils.RedisOptService;
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
 * @since 2023-12-03
 */
@RestController
@RequestMapping("/commodity")
public class CommodityController {
    @Autowired
    CommodityService service;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.commodityPath}")
    private String commodityImgPath;
    @Autowired
    RedisOptService redisOptService;
    @Autowired
    RedisLockService redisLockService;

    @ApiOperation(value = "获取商品图片", tags = "商店类")
    @GetMapping("/img")
    public AjaxJson getItemImg(@RequestParam Integer itemId) {
        return AjaxJson.getSuccessData(minioUtilS.download(String.format("%s.jpeg", itemId), commodityImgPath));
    }

    @ApiOperation(value = "增加商品", tags = "商店管理类")
    @SaCheckRole("admin")
    @PostMapping("/add")
    public AjaxJson addItem(@RequestParam MultipartFile file, @RequestParam String itemName,
                            @RequestParam Integer windowId
            , @RequestParam Double itemPrice, @RequestParam Integer amountLeft) {
        redisLockService.lock("store");
        redisOptService.deleteKey("item");
        redisLockService.unlock("store");
        Commodity f = new Commodity();
        f.setWindowId(windowId);
        f.setCommodityName(itemName);
        f.setPrice(itemPrice);
        f.setAmountLeft(amountLeft);
        if (!service.save(f)) {
            throw new RuntimeException("fail to add restaurant");
        }
        minioUtilS.upload(file, commodityImgPath, String.format("%s.jpeg", f.getCommodityId()));
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "更改商品", tags = "商店管理类")
    @SaCheckRole("admin")
    @PostMapping("/change")
    public AjaxJson changeItem(MultipartFile file, @RequestParam Integer commodityId, String itemName,
                               Integer windowId, Double itemPrice, Integer amountLeft) {
        redisLockService.lock("store");
        redisOptService.deleteKey("item");
        redisLockService.unlock("store");
        Commodity f = new Commodity();
        f.setWindowId(windowId);
        f.setCommodityName(itemName);
        f.setPrice(itemPrice);
        f.setCommodityId(commodityId);
        f.setAmountLeft(amountLeft);
        if (!service.updateById(f)) {
            throw new RuntimeException("fail to add restaurant");
        }
        if (file != null) {
            minioUtilS.upload(file, commodityImgPath, String.format("%s.jpeg", windowId));
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "删除食品", tags = "商店管理类")
    @SaCheckRole("admin")
    @PostMapping("/delete")
    public AjaxJson deleteFood(@RequestParam Integer foodId) {
        redisLockService.lock("store");
        redisOptService.deleteKey("item" + foodId);
        redisLockService.unlock("store");
        if (!service.deleteCommodity(foodId)) {
            throw new RuntimeException("fail to delete food");
        }
        return AjaxJson.getSuccess();
    }


}

