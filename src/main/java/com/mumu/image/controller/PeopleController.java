package com.mumu.image.controller;


import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.mapper.ImgMapper;
import com.mumu.image.service.PeopleService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
@RequestMapping("/people")
public class PeopleController {
    @Autowired
    private PeopleService peopleService;

    @ApiOperation(value = "更新people name", tags = "people类")
    @PostMapping("/update")
    public AjaxJson update(@RequestParam int userId, @RequestBody List<String> names) {
        return AjaxJson.getSuccessData(peopleService.checkAndInsertPeople(userId, names));
    }

    @ApiOperation(value = "删除people name", tags = "people类")
    @PostMapping("/delete")
    public AjaxJson delete(@RequestParam int userId, @RequestBody List<String> names) {
        return AjaxJson.getSuccessData(peopleService.deleteExistingPeople(userId, names));
    }

    @ApiOperation(value = "获取people", tags = "people类")
    @PostMapping("/list")
    public AjaxJson getTag(@RequestParam int userId) {
        return AjaxJson.getSuccessData(peopleService.getAllPeople(userId));
    }
}

