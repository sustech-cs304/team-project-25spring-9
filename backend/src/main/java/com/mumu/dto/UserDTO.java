package com.mumu.dto;

import com.mumu.entity.Role;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@ToString
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String userName;

    private String userPassword;

    private String userImg;

    private List<Role> userPermission;

    private String userMail;

    private String userNickname;
}
