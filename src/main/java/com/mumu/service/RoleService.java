package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.entity.Role;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mumu
 * @since 2023-10-14
 */
@Transactional
public interface RoleService extends IService<Role> {

}
