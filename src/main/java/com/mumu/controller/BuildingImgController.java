package com.mumu.controller;


import com.mumu.entity.Building;
import com.mumu.service.BuildingImgService;
import com.mumu.service.BuildingService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
@RestController
@RequestMapping("/buildingImg")
public class BuildingImgController {
    @Autowired
    private BuildingImgService buildingImgService;
    @Autowired
    private BuildingService buildingService;

    @ApiOperation(value = "通过id获取建筑的所有图片", tags = "论坛-建筑类")
    @GetMapping("/searchAllImg")
    public AjaxJson allBuildingsImg(@RequestParam int buildingId) {
        //Fot testing the concrete class
        System.out.println(buildingImgService.getClass().getName());
        return AjaxJson.getSuccessData(buildingImgService.allBuildingImg(buildingId));
    }

    @ApiOperation(value = "删除一张建筑图片", tags = "论坛-建筑类")
    @GetMapping("/deleteImg")
    public AjaxJson deleteImg(@RequestParam int buildingId, @RequestParam String imgName) {
        System.out.println(buildingImgService.getClass().getName());
        // check
        Building building = buildingService.oneBuildingById(buildingId);
        if (building == null)
            return AjaxJson.getError("Fail to find the building");
        String result = buildingImgService.deleteOneImg(buildingId, imgName);
        if (!result.equals("删除文件成功"))
            return AjaxJson.getError("删除文件失败");
        else
            return AjaxJson.getSuccessData(building.getBuildingImg() - 1);
    }

    @ApiOperation(value = "删除所有建筑图片", tags = "论坛-建筑类")
    @GetMapping("/deleteAllImg")
    public AjaxJson deleteAllImg(@RequestParam int buildingId) {
        System.out.println(buildingImgService.getClass().getName());
        // check
        Building building = buildingService.oneBuildingById(buildingId);
        if (building == null)
            return AjaxJson.getError("Fail to find the building");
        String result = buildingImgService.deleteImgByBuildingId(buildingId);
        if (!result.equals("删除文件成功"))
            return AjaxJson.getError("删除文件失败");
        else
            return AjaxJson.getSuccess("删除文件成功");
    }
}
