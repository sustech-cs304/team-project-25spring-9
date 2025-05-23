package com.mumu.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfoDTO {
    private Integer userId;

    private String userName;

    private String userImg;

    private String userNickname;
}
