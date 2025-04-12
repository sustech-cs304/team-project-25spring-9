package com.mumu.controller;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.mumu.entity.User;
import com.mumu.service.UserService;
import com.mumu.utils.AjaxJson;
import com.mumu.utils.MinioUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mumu
 * @since 2023-09-22
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService service;
    @Autowired
    private MinioUtils minioUtilS;
    @Value("${minio.userPath}")
    private String userImgPath;
    @Autowired
    JavaMailSender javaMailSender;

    @ApiOperation(value = "发送邮件", notes = "仅测试使用", tags = "测试类")
    @GetMapping("/test/sendn")//todo:delete this cell
    public String sendHtmlEmail() {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            String verifyCode = "HIB1V7V";
            String htmlMsg = "<div style='background-color: #e1f5fe; padding: 20px;'>"
                    + "<h2>Welcome to SUSTech Campus!</h2>"
                    + "<p>Thank you for your interest. SUSTech Campus is a xxxxxxxxxxxx.</p>"//todo:change the html format
                    + "<p><strong>Below is your verification code:</strong></p>"
                    + "<div style='background-color: #ffffff;border: 2px solid #0277bd; padding: 5px; margin: 5px 0; text-align: center;'>"
                    + "<h3>" + verifyCode + "</h3>"
                    + "</div>"
                    + "<p>Please enter the above code to complete your registration.</p>"
                    + "<p>Explore our platform: <a href='https://pix2text.com'>SUSTech Campus</a></p>"
                    + "<p>If you didn't request this, please ignore this email.</p>"
                    + "</div>";
            helper.setText(htmlMsg, true); // 设置为true表示启用HTML格式的邮件
            helper.setTo("2497440459@qq.com");
            helper.setSubject("SUSTech Campus Verification Code");
            helper.setFrom("reset_no_reply@foxmail.com");

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return "send fail";
        }
        return "send success";
    }

    @ApiOperation(value = "更新昵称", tags = "用户类")
    @GetMapping("/updatenickname")
    public AjaxJson updateNickName(@RequestParam String nickname) {
        return service.setUserNickName(Integer.parseInt((String) StpUtil.getLoginId()), nickname);
    }

    @ApiOperation(value = "更新邮箱", tags = "用户类")
    @GetMapping("/updatemail")
    public AjaxJson updateMail(@RequestParam String mail) {
        return service.setUserMail(Integer.parseInt((String) StpUtil.getLoginId()), mail);
    }

    @ApiOperation(value = "发送重置邮件", tags = "用户类")
    @GetMapping("/sendresetemail")
    public AjaxJson sendResetEmail(@RequestParam String UserName) {
        return service.resetPassword(UserName);
    }

    @ApiOperation(value = "通过重置代码重置", tags = "用户类")
    @GetMapping("/setpassword")
    public AjaxJson setPassword(@RequestParam String code, String password) {
        return service.resetPassword(code, password);
    }

    @ApiOperation(value = "下载用户图片，id为空时下载当前用户头像", tags = "用户类")
    @GetMapping("/userimg/download")
    public AjaxJson download(String userId) {
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        return AjaxJson.getSuccessData(minioUtilS.download(String.format("%s.jpeg", userId), userImgPath));
    }

    @ApiOperation(value = "上传用户图片", tags = "用户类")
    @SaCheckLogin
    @PostMapping("/userimg/upload")
    public AjaxJson upload(MultipartFile file) {
        minioUtilS.upload(file, userImgPath, String.format("%s.jpeg", StpUtil.getLoginId()));
        return AjaxJson.getSuccess();
    }


    //- http://localhost:9090/user/signin
    @ApiOperation(value = "登录", tags = "用户类")
    @GetMapping("/signin")
    public AjaxJson login(User user) {
        if (!service.isusernameExists(user.getUserName())) {
            return AjaxJson.get(501, "用户名不存在");
        } else if (!service.isusernameExists(user.getUserName(), user.getUserPassword())) {
            return AjaxJson.get(502, "用户名或密码错误");
        } else {
            StpUtil.login(service.getUserId(user.getUserName()));
            return AjaxJson.getSuccess(String.valueOf(service.getUserId(user.getUserName())));
        }
    }

    @ApiOperation(value = "管理员登录", tags = "测试类")
    @GetMapping("/authorized")
    public AjaxJson authorize() {
        StpUtil.login(service.getUserId("admin"));
        return AjaxJson.getSuccess();
    }

    //- http://localhost:9090/user/logout
    @ApiOperation(value = "登出", tags = "用户类")
    @GetMapping("/logout")
    public AjaxJson logout() {
        StpUtil.logout();
        return AjaxJson.getSuccess();
    }

    //- http://localhost:9090/user/list
    @ApiOperation(value = "显示全部用户", tags = "管理员类")
    @SaCheckRole("admin")
    @GetMapping("/list")
    public AjaxJson full_list() {
        return AjaxJson.getSuccessData(service.selectList());
    }

    @ApiOperation(value = "修改密码", tags = "用户类")
    @SaCheckLogin
    @GetMapping("/changePassword")
    public AjaxJson changePassword(@RequestParam String password, @RequestParam String newPassword) {
        return service.changePassowrd(Integer.parseInt((String) StpUtil.getLoginId()), password, newPassword);
    }

    //- http://localhost:9090/user/new
    @ApiOperation(value = "用户注册", tags = "用户类")
    @GetMapping("/new")
    public AjaxJson add_user(User user) {
        if (!service.isusernameExists(user.getUserName())) {
            if (service.save(user)) {
                return AjaxJson.getSuccess();
            }
            return AjaxJson.get(504, "注册时出现未知错误！");
        }
        return AjaxJson.get(503, "用户名重复！");

    }

    @ApiOperation(value = "检查是否登录", tags = "用户类")
    @SaCheckLogin
    @GetMapping("/islogin")
    public AjaxJson islogin() {
        return AjaxJson.getSuccessData(service.getUser(Integer.parseInt((String) StpUtil.getLoginId())));
    }

    @ApiOperation(value = "查询用户名是否存在", tags = "用户类")
    @GetMapping("/username")
    public AjaxJson name(User user) {
        if (!service.isusernameExists(user.getUserName())) {
            return AjaxJson.get(501, "用户名不存在");
        } else {
            return AjaxJson.getSuccess();
        }
    }

    @ApiOperation(value = "批量注册", tags = "管理员类")
    @SaCheckRole("admin")
    @GetMapping("/signuplist")
    public AjaxJson signupList(@RequestParam List<String> UserName) {
        List<AjaxJson> rr = new ArrayList<>();
        for (String name : UserName) {
            try {
                service.save(new User(name, name));
            } catch (Exception e) {
                rr.add(new AjaxJson(500, String.format("%s failed in signin,cuased by %s", name, e.getCause()), name, null));
                e.printStackTrace();
            }
        }
        if (rr.isEmpty()) {
            return AjaxJson.getSuccess("success!");
        } else {
            return new AjaxJson(500, "部分插入失败", rr, (long) rr.size());
        }
    }

    @ApiOperation(value = "批量删除", tags = "管理员类")
    @SaCheckRole("admin")
    @GetMapping("/deletelist")
    public AjaxJson deleteList(@RequestParam List<String> UserName) {
        List<AjaxJson> rr = new ArrayList<>();
        for (String name : UserName) {
            try {
                service.remove(new MPJLambdaWrapper<User>().eq(User::getUserName, name));
            } catch (Exception e) {
                rr.add(new AjaxJson(500, String.format("%s failed in delete, cuased by %s", name, e.getCause()), name, null));
                e.printStackTrace();
            }
        }
        if (rr.isEmpty()) {
            return AjaxJson.getSuccess("success!");
        } else {
            return new AjaxJson(500, "部分删除失败", rr, (long) rr.size());
        }
    }


    @ApiOperation(value = "重置账户", tags = "管理员类")
    @SaCheckRole("admin")
    @GetMapping("/reset")
    public AjaxJson reset(User user) {
        return service.resetPassword(user);
    }
    @ApiOperation(value = "获取用户信息", tags = "用户类")
    @GetMapping("/info")
    public AjaxJson getUserInformation(String userId) {
        if (userId == null) {
            userId = (String) StpUtil.getLoginId();
        }
        return service.getUserInformation(userId);
    }
}

