package com.mumu.controller;


import com.mumu.service.PermissionService;
import com.mumu.utils.AjaxJson;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-10-15
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    PermissionService service;

    //    @SaCheckRole("admin")
    @ApiOperation(value = "设置权限", tags = "管理员类")
    @GetMapping("/givepower")
    public AjaxJson powerList(@RequestParam List<String> UserName, @RequestParam List<Integer> UserPower) {
        List<AjaxJson> rr = new ArrayList<>();
        for (String name : UserName) {
            try {
                service.insertPermission(name, UserPower);
            } catch (Exception e) {
                rr.add(new AjaxJson(500, String.format("%s failed in signin,cuased by %s", name, e.getCause()), name, null));
                e.printStackTrace();
            }
        }
        if (rr.isEmpty()) {
            return AjaxJson.getSuccess();
        } else {
            return new AjaxJson(500, "部分权限更新失败", rr, (long) rr.size());
        }
    }

    @ApiOperation(value = "设置权限", tags = "管理员类")
    @GetMapping("/removepower")
    public AjaxJson deletePower(@RequestParam String UserName, @RequestParam Integer UserPower) {
        service.deletePermission(UserName, Arrays.asList(UserPower));
        return AjaxJson.getSuccess();
    }

    @ApiOperation(value = "设置权限", tags = "管理员类")
    @GetMapping("/addpower")
    public AjaxJson addPower(@RequestParam String UserName, @RequestParam Integer UserPower) {
        service.insertPermission(UserName, Arrays.asList(UserPower));
        return AjaxJson.getSuccess();
    }
}



