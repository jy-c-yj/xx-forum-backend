package com.cyj.xx.dvo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserPersonalDVO {
    private Long id;
    private String username;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;
    private Integer likeCount;
    private Integer postCount;
    private String image;
    private String gender;
    private String phone;
    private String desc;
}
