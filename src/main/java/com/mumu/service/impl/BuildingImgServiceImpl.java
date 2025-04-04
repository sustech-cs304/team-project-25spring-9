package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.entity.Building;
import com.mumu.entity.BuildingImg;
import com.mumu.mapper.BuildingImgMapper;
import com.mumu.service.BuildingImgService;
import com.mumu.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-10-16
 */
@Service
public class BuildingImgServiceImpl extends ServiceImpl<BuildingImgMapper, BuildingImg> implements BuildingImgService {
    @Autowired
    BuildingImgMapper mapper;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.buildingPath}")
    private String buildingImgPath;

    @Override
    // 获取建筑最大的图片的名字
    public String firstImgIdGroupByBuildingId(int buildingId) {
        MPJLambdaWrapper<BuildingImg> queryWrapper = new MPJLambdaWrapper<BuildingImg>()
                .select(BuildingImg::getImgName)
                .eq(BuildingImg::getBuildingId, buildingId)
                .orderByAsc("buildingImg_id")
                .last("limit 1");
        return mapper.selectJoinOne(String.class, queryWrapper);
    }

    @Override
    // 获取建筑最大的图片的名字
    public String largestImgIdGroupByBuildingId(int buildingId) {
        MPJLambdaWrapper<BuildingImg> queryWrapper = new MPJLambdaWrapper<BuildingImg>()
                .select(BuildingImg::getImgName)
                .eq(BuildingImg::getBuildingId, buildingId)
                .orderByDesc("buildingImg_id")
                .last("limit 1");
        return mapper.selectJoinOne(String.class, queryWrapper);
    }

    @Override
    // 通过id获取建筑的所有图片
    public List<String> allBuildingImg(int buildingId) {
        MPJLambdaWrapper<BuildingImg> queryWrapper = new MPJLambdaWrapper<BuildingImg>()
                .select(BuildingImg::getImgName)
                .eq(BuildingImg::getBuildingId, buildingId);
        return mapper.selectJoinList(String.class, queryWrapper);
    }

    @Override
    // 删除一张建筑图片
    public String deleteOneImg(int buildingId, String imgName) {
        String result = minioUtilS.removeFile(buildingImgPath, imgName);
        if (!result.equals("删除文件成功")) {
            return "删除文件失败";
        }
        mapper.deleteJoin(JoinWrappers.delete(BuildingImg.class)
                .delete(BuildingImg.class)
                .eq(Building::getBuildingId, buildingId)
                .eq(BuildingImg::getImgName, imgName));
        return "删除文件成功";
    }

    @Override
    // 删除所有建筑图片
    public String deleteImgByBuildingId(int buildingId) {
        List<String> imgNameList = allBuildingImg(buildingId);
        for (String imgName : imgNameList) {
            String result = deleteOneImg(buildingId, imgName);
            if (!result.equals("删除文件成功")) {
                return "删除文件失败";
            }
        }
        return "删除文件成功";
    }
}
