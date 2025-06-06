package com.mumu.image.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mumu.image.entity.Album;
import com.mumu.image.entity.Img;
import com.mumu.image.entity.People;
import com.mumu.image.mapper.AlbumMapper;
import com.mumu.image.service.AlbumService;
import com.mumu.image.service.ImgService;
import com.mumu.image.service.TagService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumu
 * @since 2025-05-06
 */
@RestController
@RequestMapping("/album")
public class AlbumController {
    @Autowired
    private AlbumService albumService;
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private ImgService imgService;
    @ApiOperation(value = "更新相册 name", tags = "相册类")
    @PostMapping("/update")
    public AjaxJson update(@RequestParam int userId,
                           @RequestParam int albumId,
                           @RequestParam String names,
    @RequestParam String albumDescription) {
        return AjaxJson.getSuccessData(albumMapper.updateById(new Album(userId,albumId,names,albumDescription)));
    }
    @ApiOperation(value = "新建相册 name", tags = "相册类")
    @PostMapping("/new")
    public AjaxJson insert(@RequestParam int userId,
                           @RequestParam String names,
                           @RequestParam String albumDescription) {
        return AjaxJson.getSuccessData(albumMapper.insert(new Album(userId,names,albumDescription)));
    }
    @ApiOperation(value = "获取相册", tags = "相册类")
    @PostMapping("/list")
    public AjaxJson getTag(@RequestParam int userId) {
        return AjaxJson.getSuccessData(
                albumMapper.selectList( new LambdaQueryWrapper<Album>()
                .eq(Album::getUserId, userId)));
    }
    @ApiOperation(value = "删除相册 name", tags = "相册类")
    @PostMapping("/delete")
    public AjaxJson delete(@RequestParam int userId, @RequestParam int albumId) {
        imgService.update(new LambdaUpdateWrapper<Img>()
                .eq(Img::getUserId, userId)  // 条件：userId == user1
                .eq(Img::getAlbumId, albumId)    // 条件：imgId == img1
                .set(Img::getAlbumId, null)   // 更新 valid 字段为 false
        );
        return AjaxJson.getSuccessData(albumMapper.delete(new LambdaUpdateWrapper<Album>()
                .eq(Album::getUserId, userId).eq(Album::getAlbumId, albumId)));
    }
}

