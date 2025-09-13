package com.cyj.xx.dvo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserSignInDVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 签到状态: 0=未签到,1=已签到
     */
    private Boolean status;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 最后一次签到日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSignDate;


}
