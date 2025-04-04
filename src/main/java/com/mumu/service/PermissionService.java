package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.Permission;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-15
 */
@Transactional
public interface PermissionService extends IService<Permission> {

    boolean insertPermission(String name, List<Integer> permission);

    boolean deletePermission(String name, List<Integer> permission);
}
