package com.mumu.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;

@Component
public class MyMailService {

    @Autowired
    JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    String mailSender;

    public AjaxJson sendResetMail(String verifyCode, String to) {
        String htmlMsg = "<div style='background-color: #e1f5fe; padding: 20px;'>"
                + "<h2>Welcome to Smart Photo Album!</h2>"
                + "<p>Thank you for your interest.</p>"//todo:change the html format
                + "<p><strong>Below is your verification code:</strong></p>"
                + "<div style='background-color: #ffffff;border: 2px solid #0277bd; padding: 5px; margin: 5px 0; text-align: center;'>"
                + "<h3>" + verifyCode + "</h3>"
                + "</div>"
                + "<p>Please enter the above code to complete your registration.</p>"
                + "<p>Explore our platform: <a href='http://8.134.53.236:8080/forgetPassword/" + verifyCode + "'>SUSTech Campus</a></p>"//
                + "<p>If you didn't request this, please ignore this email.</p>"
                + "</div>";
        return sendMail(mailSender, to, "SUSTech Campus Verification Code", htmlMsg);
    }

    public AjaxJson sendMail(String from, String to, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(text, true); // 设置为true表示启用HTML格式的邮件
            helper.setTo(to);
            helper.setSubject(subject);
            System.out.println(from);
            helper.setFrom(from);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxJson.getError("Fail to send email");
        }
        return AjaxJson.getError("Success!");
    }
}
