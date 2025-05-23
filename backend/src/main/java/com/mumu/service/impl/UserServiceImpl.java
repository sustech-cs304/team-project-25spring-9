package com.mumu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.dto.UserDTO;
import com.mumu.dto.UserInfoDTO;
import com.mumu.entity.Permission;
import com.mumu.entity.Role;
import com.mumu.entity.User;
import com.mumu.mapper.UserMapper;
import com.mumu.service.UserService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MyMailService;
import com.mumu.utils.RedisOptService;
import com.mumu.utils.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mumu
 * @since 2023-09-22
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    UserMapper mapper;
    @Autowired
    RedisOptService redisOptService;
    @Autowired
    VerificationCodeGenerator verificationCode;
    @Autowired
    MyMailService mailService;

    private static Map<String, String> resetPasswordCode = new HashMap<>();

    @Override
    public AjaxJson setUserNickName(int userid, String nickName) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userid)
                .set(User::getUserNickname, nickName);
        return 1 == mapper.update(null, updateWrapper) ? AjaxJson.getSuccess() : AjaxJson.getError("Error!");
    }

    @Override
    public AjaxJson setUserMail(int userid, String mail) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userid)
                .set(User::getUserMail, mail);
        return 1 == mapper.update(null, updateWrapper) ? AjaxJson.getSuccess() : AjaxJson.getError("Error!");
    }


    @Override
    public UserDTO getUser(int userid) {
        UserDTO ud = (UserDTO) redisOptService.get("user" + userid);
        if (ud != null)
            return ud;
        MPJLambdaWrapper<User> queryWrapper = new MPJLambdaWrapper<User>()
                .selectAll(User.class)
                .selectCollection(Role.class, UserDTO::getUserPermission)
                .leftJoin(Permission.class, Permission::getUserId, User::getUserId)
                .leftJoin(Role.class, Role::getRoleId, Permission::getRoleId)
                .eq(User::getUserId, userid);
        ud = mapper.selectJoinOne(UserDTO.class, queryWrapper);
        redisOptService.set("user" + userid, ud);
        return ud;
    }

    @Override
    public AjaxJson changePassowrd(Integer userId, String password, String newpassword) {
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getUserId, userId)
                .eq(User::getUserPassword, password)
                .set(User::getUserPassword, newpassword);
        return 1 == mapper.update(null, updateWrapper) ? AjaxJson.getSuccess() : AjaxJson.getError("wrong password");

    }

    @Override
    public List<User> selectList() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        return mapper.selectList(queryWrapper);
    }

    @Override
    public boolean isusernameExists(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return mapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public Integer getUserId(String userName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        return mapper.selectList(queryWrapper).get(0).getUserId();
    }

    @Override
    public boolean isusernameExists(String userName, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", userName);
        queryWrapper.eq("user_password", password);
        return mapper.selectCount(queryWrapper) > 0;
    }

    /**
     * reset to a certain password
     *
     * @param user
     * @return
     */
    @Override
    public AjaxJson resetPassword(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(user.getUserId() != null, User::getUserId, user.getUserId())
                .eq(user.getUserName() != null, User::getUserName, user.getUserName());
        switch (mapper.selectCount(queryWrapper)) {
            case 0:
                return AjaxJson.getError("no user found");
            case 1:
                LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(user.getUserId() != null, User::getUserId, user.getUserId())
                        .eq(user.getUserName() != null, User::getUserName, user.getUserName());
                updateWrapper.set(User::getUserPassword, user.getUserPassword());
                return 1 == mapper.update(null, updateWrapper) ? AjaxJson.getSuccess() : AjaxJson.getError("error occurred");
            default:
                return AjaxJson.getError("too many users found,please give the userId");
        }
    }

    /**
     * send reset mail
     */
    @Override
    public AjaxJson resetPassword(String UserName) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(User::getUserName, UserName);
        String tomail = mapper.selectOne(queryWrapper).getUserMail();
        String code = verificationCode.get();
        AjaxJson reply = mailService.sendResetMail(code, tomail);
        resetPasswordCode.put(code, UserName);
        return reply;
    }

    /**
     * use code to reset password
     */
    @Override
    public AjaxJson resetPassword(String code, String password) {
        String UserName;
        UserName = resetPasswordCode.get(code);
        if (UserName == null) {
            return AjaxJson.getError("verification code error!");
        }
        User u = new User();
        u.setUserName(UserName);
        u.setUserPassword(password);
        AjaxJson aj = resetPassword(u);
        if (aj.code == 200) {
            resetPasswordCode.remove(code);
        }
        return aj;
    }

    @Override
    public AjaxJson getUserInformation(String userId) {
        MPJLambdaWrapper<User> mpjLambdaWrapper = new MPJLambdaWrapper<User>()
                .selectAll(User.class)
                .eq(User::getUserId, userId);
        return AjaxJson.getSuccessData(mapper.selectJoinOne(UserInfoDTO.class, mpjLambdaWrapper));
    }

    @Override
    public boolean isAdmin(int userId) {
        MPJLambdaWrapper<User> mpjLambdaWrapper = new MPJLambdaWrapper<User>()
                .eq(User::getUserId, userId)
                .select(Permission::getPermissionId)
                .leftJoin(Permission.class, Permission::getUserId, User::getUserId)
                .leftJoin(Role.class, Role::getRoleId, Permission::getRoleId)
                .eq(Role::getRoleName, "admin");
        List<Integer> permissionIds = mapper.selectJoinList(Integer.class, mpjLambdaWrapper);
        return !permissionIds.isEmpty();
    }

    @Override
    public boolean isBlack(int userId) {
        MPJLambdaWrapper<User> mpjLambdaWrapper = new MPJLambdaWrapper<User>()
                .eq(User::getUserId, userId)
                .select(Permission::getPermissionId)
                .leftJoin(Permission.class, Permission::getUserId, User::getUserId)
                .leftJoin(Role.class, Role::getRoleId, Permission::getRoleId)
                .eq(Role::getRoleName, "black");
        List<Integer> permissionIds = mapper.selectJoinList(Integer.class, mpjLambdaWrapper);
        return !permissionIds.isEmpty();
    }
}
