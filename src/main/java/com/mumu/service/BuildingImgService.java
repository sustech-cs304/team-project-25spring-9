package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.BuildingImg;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
public interface BuildingImgService extends IService<BuildingImg> {
    String firstImgIdGroupByBuildingId(int buildingId); // 获取建筑第一张图片的名字

    String largestImgIdGroupByBuildingId(int buildingId);   // 获取建筑最大的图片的名字

    List<String> allBuildingImg(int buildingId);    // 通过id获取建筑的所有图片

    String deleteOneImg(int buildingId, String imgName);    // 删除一张建筑图片

    String deleteImgByBuildingId(int buildingId);   // 删除所有建筑图片
}
