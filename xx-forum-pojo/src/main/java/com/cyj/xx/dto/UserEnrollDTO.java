package com.cyj.xx.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
@Builder
public class UserEnrollDTO implements Serializable {
    private String username;
    private String password;
    private String email = null;
    private MultipartFile file = null;


}
