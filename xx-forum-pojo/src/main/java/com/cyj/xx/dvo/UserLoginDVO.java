package com.cyj.xx.dvo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class UserLoginDVO implements Serializable {
    private String username;
    private String password;
    private String email = null;
    private String file = null;

    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createTime;
    
    private String token;
    private String postCount;
    private Integer likeCount;


}
