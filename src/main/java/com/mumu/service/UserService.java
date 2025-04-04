package com.mumu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mumu.dto.UserDTO;
import com.mumu.entity.User;
import com.mumu.utils.AjaxJson;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mumu
 * @since 2023-09-22
 */
public interface UserService extends IService<User> {

    AjaxJson setUserNickName(int userid, String nickName);

    AjaxJson setUserMail(int userid, String mail);

    UserDTO getUser(int id);

    AjaxJson changePassowrd(Integer userId, String password, String newpassword);

    List<User> selectList();

    boolean isusernameExists(String userName);

    Integer getUserId(String userName);

    boolean isusernameExists(String userName, String password);

    AjaxJson resetPassword(User user);

    AjaxJson resetPassword(String UserName);

    AjaxJson resetPassword(String code, String password);

    AjaxJson getUserInformation(String userId);

    boolean isAdmin(int userId);

    boolean isBlack(int userId);
}
