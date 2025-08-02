package com.cyj.xx.dvo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class UserLoginDVO implements Serializable {
    private Long id;
    private String username;
    private String email = null;
    private String file = null;
    private String phone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Integer status;
    private String desc;
    private String gender;
    private String token;
    private Integer postCount;
    private Integer likeCount;


}
