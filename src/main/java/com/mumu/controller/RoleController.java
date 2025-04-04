package com.mumu.controller;


import com.mumu.service.RoleService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-10-14
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    RoleService service;

}

