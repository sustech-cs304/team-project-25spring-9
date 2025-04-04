package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.dto.*;
import com.mumu.entity.Building;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
public interface BuildingService extends IService<Building> {
    List<BuildingDTO> selectList(); // 查询所有信息（建筑、评论、回复）

    BuildingDTO findById(int id);   // 根据建筑id查询所有信息（建筑、评论、回复）

    Integer getIdByName(String name);   // 通过名字获取建筑id

    String getIdByLocation(double x, double y); // 通过坐标获取建筑名字

    Building oneBuildingById(int id);   // 通过建筑id获取建筑信息

    int deleteById(int id); // 通过建筑id删除所有相关信息（建筑、评论、回复）

    List<BuildingInfoDTO> allBuildings();   // 获取所有建筑的id和名称

    List<BuildingNameDTO> allBuildingsFirst();  // 获取所有建筑的名称和第一张图片

    List<BuildingLocationDTO> allBuildingsLocation();   // 获取所有建筑的id，名称，坐标

    List<Building> allBuildingsInfo();  // 获取所有建筑的全部信息

    List<BuildingWithImgDTO> allBuildingsInfoWithImg(); // 获取所有建筑的全部信息以及最后一张图片

    BuildingCommentsDTO commentsRelatedById(int id);  // 通过建筑id获取建筑信息以及有关话题

    Building oneBuildingByName(String name);    // 通过建筑名称获取建筑信息

    List<Building> topBuildingsOrderByComments(int num);    // 热度前十（按照点赞数量排序）

    List<Integer> searchByKeyWords(String keyWords);    // 关键词检索

    List<Integer> searchBuildingByKeyWords(String keyWords);    // 关键词检索，只返回建筑名称匹配的建筑id
}
