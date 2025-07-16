package com.cyj.xx.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReviseDTO {


    private String username;

    private String password;

    private String email;

    private String image = null;

    private String gender;

    private String phone;

    private String desc;




}
