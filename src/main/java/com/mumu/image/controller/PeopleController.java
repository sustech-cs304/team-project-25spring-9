package com.mumu.image.controller;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mumu.image.DTO.ImgDTO;
import com.mumu.image.entity.People;
import com.mumu.image.mapper.ImgMapper;
import com.mumu.image.mapper.PeopleMapper;
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
    @Autowired
    private PeopleMapper peopleMapper;

    @ApiOperation(value = "更新people name", tags = "people类")
    @PostMapping("/update")
    public AjaxJson update(@RequestParam int userId, @RequestBody List<String> names) {
        return AjaxJson.getSuccessData(peopleService.checkAndInsertPeople(userId, names));
    }

    @ApiOperation(value = "修改people name", tags = "people类")
    @PostMapping("/change")
    public AjaxJson change(@RequestParam int userId, @RequestParam String name,@RequestParam String nickname) {
        return AjaxJson.getSuccessData(peopleService.update(new LambdaUpdateWrapper<People>()
                .eq(People::getUserId,userId)
                .eq(People::getName,name)
                .set(People::getNickname,nickname)
        ));
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

