package com.mumu.image.controller;


import com.mumu.image.service.ImgPeopleService;
import com.mumu.image.service.ImgTagService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/imgpeople")
public class ImgPeopleController {
@Autowired
    private ImgPeopleService imgPeopleService;
    @ApiOperation(value = "删除图片people", tags = "图片类")
    @PostMapping("/delete")
    public AjaxJson delete(int imgId, int userId, String  people) {
        return AjaxJson.getSuccessData(imgPeopleService.deleteImgPeople(userId, imgId, people));
    }
    @ApiOperation(value = "添加图片people", tags = "图片类")
    @PostMapping("/add")
    public AjaxJson add(int imgId, int userId, String  people) {
        return AjaxJson.getSuccessData(imgPeopleService.addImgPeople(userId, imgId, people));
    }
}

