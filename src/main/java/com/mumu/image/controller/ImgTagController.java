package com.mumu.image.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mumu.image.entity.Img;
import com.mumu.image.service.ImgTagService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/imgtag")
public class ImgTagController {
    @Autowired
    private ImgTagService imgTagService;
    @ApiOperation(value = "删除图片tag", tags = "图片类")
    @PostMapping("/delete")
    public AjaxJson delete(int imgId, int userId, String  tag) {
        return AjaxJson.getSuccessData(imgTagService.deleteImgTag(userId, imgId, tag));
    }
    @ApiOperation(value = "添加图片tag", tags = "图片类")
    @PostMapping("/add")
    public AjaxJson add(int imgId, int userId, String  tag) {
        return AjaxJson.getSuccessData(imgTagService.addImgTag(userId, imgId, tag));
    }
}

