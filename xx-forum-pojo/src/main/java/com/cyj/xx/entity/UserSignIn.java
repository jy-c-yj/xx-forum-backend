package com.cyj.xx.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserSignIn {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 板块ID
     */
    private Long sectionId;

    /**
     * 签到状态: 0=未签到,1=已签到
     */
    private Boolean status;

    /**
     * 连续签到天数
     */
    private Integer continuousDays;

    /**
     * 经验值
     */
    private  Integer experience;

    /**
     * 是否删除: 0=正常,1=删除
     */
    private int isDeleted;

    /**
     * 最后一次签到日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSignDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;




}
