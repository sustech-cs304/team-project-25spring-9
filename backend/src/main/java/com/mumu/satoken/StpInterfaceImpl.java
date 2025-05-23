package com.mumu.satoken;

import cn.dev33.satoken.stp.StpInterface;
import com.mumu.entity.Role;
import com.mumu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component    // 打开此注解，保证此类被springboot扫描，即可完成sa-token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    UserService service;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        System.out.println(loginId);
        System.out.println(loginType);
        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询权限
        List<String> list = new ArrayList<String>();
//        list.add("user-update");
//        list.add("user-get");
//        list.add("article-get");
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return getRoleList(loginId);
    }

    //    @Cacheable(value = "role", key = "#loginId")
    public List<String> getRoleList(Object loginId) {
        List<String> list = new ArrayList<String>();
        System.out.println(loginId);
        for (Role r :
                service.getUser(Integer.parseInt((String) loginId)).getUserPermission()) {
            list.add(r.getRoleName());
        }
        return list;
    }
}
