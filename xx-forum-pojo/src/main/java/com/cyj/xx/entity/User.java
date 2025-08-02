package com.cyj.xx.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;


    private String username;


    private String password;


    private String email;


    private String image = "default_avatar.png";

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updatedTime;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime lastLoginTime;


    private Integer status;

    private String gender;

    private String phone = "还未绑定号码";

    private String desc ="这个人很懒，什么都没写";

    private Integer postCount;

    private Integer likeCount;



}
