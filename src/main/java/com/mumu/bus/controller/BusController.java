package com.mumu.bus.controller;


import cn.dev33.satoken.annotation.SaCheckRole;
import com.mumu.bus.entity.Bus;
import com.mumu.bus.service.BusService;
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
 * @since 2023-11-05
 */
@RestController
@RequestMapping("/bus")
public class BusController {
    @Autowired
    BusService service;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.busPath}")
    private String busJsonPath;

    @ApiOperation(value = "获取公交线路", tags = "公交类")
    @GetMapping("/path")
    public AjaxJson getBusPath(@RequestParam Integer busId) {
        return AjaxJson.getSuccessData(minioUtilS.download(String.format("%s.json", busId), busJsonPath));
    }

    @ApiOperation(value = "获取全部公交信息", tags = "公交类")
    @GetMapping("/all")
    public AjaxJson getBusPath() {
        return AjaxJson.getSuccessData(service.getAllBus());
    }

    @ApiOperation(value = "增加公交线路", tags = "公交类")
    @PostMapping("/new")
    public AjaxJson addBus(@RequestBody MultipartFile file, @RequestParam String busName) {
        Bus b = new Bus();
        b.setBusName(busName);
        if (!service.save(b)) {
            throw new RuntimeException("fail to add a new bus");
        }
        minioUtilS.upload_old(file, busJsonPath, String.format("%s.json", b.getBusId()));
        return AjaxJson.getSuccessData(b);
    }

    @ApiOperation(value = "更改公交线路", tags = "公交类")
    @PostMapping("/change")
    public AjaxJson changeBus(@RequestBody MultipartFile file, @RequestParam Integer id) {
        minioUtilS.upload_old(file, busJsonPath, String.format("%s.json", id));
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "获取删除的公交信息", tags = "公交类")
    @GetMapping("/recycle")
    public AjaxJson getDeletedBusPath() {
        return AjaxJson.getSuccessData(service.getDeletedBus());
    }

    @ApiOperation(value = "删除公交", tags = "公交类")
    @GetMapping("/delete")
    public AjaxJson deleteRestaurant(@RequestParam Integer busId) {
        if (!service.deleteBus(busId)) {
            throw new RuntimeException("fail to delete bus");
        }
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "恢复公交", tags = "公交类")
    @SaCheckRole("admin")
    @GetMapping("/recover")
    public AjaxJson recoverRestaurant(@RequestParam Integer busId) {
        if (!service.recoverBus(busId)) {
            throw new RuntimeException("fail to delete bus");
        }
        return AjaxJson.getSuccess();
    }

}

