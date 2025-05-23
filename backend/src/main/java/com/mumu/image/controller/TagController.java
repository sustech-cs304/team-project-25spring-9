package com.mumu.image.controller;


import com.mumu.image.service.TagService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumu
 * @since 2025-03-15
 */
@RestController
@RequestMapping("/tag")
public class TagController {
    @Autowired
    private TagService tagService;
    @ApiOperation(value = "更新tag name", tags = "tag类")
    @PostMapping("/updatetest")
    public AjaxJson update(@RequestParam int userId, @RequestBody List<String> names) {
        return AjaxJson.getSuccessData(tagService.checkAndInsertTag(userId, names));
    }
    @ApiOperation(value = "获取tag", tags = "tag类")
    @PostMapping("/list")
    public AjaxJson getTag(@RequestParam int userId) {
        return AjaxJson.getSuccessData(tagService.getAllTag( userId));
    }
    @ApiOperation(value = "删除tag name", tags = "tag类")
    @PostMapping("/delete")
    public AjaxJson delete(@RequestParam int userId, @RequestBody List<String> names) {
        return AjaxJson.getSuccessData(tagService.deleteExistingTag(userId, names));
    }
}

