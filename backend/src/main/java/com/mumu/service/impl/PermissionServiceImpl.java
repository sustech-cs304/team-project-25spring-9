package com.mumu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.entity.Permission;
import com.mumu.mapper.PermissionMapper;
import com.mumu.service.PermissionService;
import com.mumu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-10-15
 */
@Service
@Transactional
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Autowired
    PermissionMapper mapper;
    @Autowired
    UserService userService;

    @Override
    public boolean insertPermission(String name, List<Integer> permission) {
        Integer id = userService.getUserId(name);
        Boolean tt = true;
        for (Integer a : permission) {
            mapper.insert(new Permission(id, a));
        }
        return tt;
    }
    @Override
    public boolean deletePermission(String name, List<Integer> permission){
        Integer id = userService.getUserId(name);
        Boolean tt = true;
        for (Integer a : permission) {
            mapper.delete(new MPJLambdaWrapper<Permission>().eq(Permission::getPermissionId,a).eq(Permission::getUserId,id));
        }
        return tt;
    }
}
