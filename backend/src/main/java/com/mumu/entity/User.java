package com.mumu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mumu
 * @since 2023-09-22
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class User implements Serializable {
    public User(String userName,String userPassword){
        this.userName = userName;
        this.userPassword = userPassword;
        this.userMail = userName + "@mail.sustech.edu.cn";

    }

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String userName;

    private String userPassword;

    private String userImg;

    private String userMail;

    private String userNickname;

    private Boolean userValid;

}
