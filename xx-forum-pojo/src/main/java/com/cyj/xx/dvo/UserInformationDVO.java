package com.cyj.xx.dvo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInformationDVO {

    private Long id;

    private String username;

    private String image = "default_avatar.png";

    private String postCount;

    private Integer likeCount;


}
