package com.cyj.xx.dvo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ManageShowSectionDVO {
    /**
     * 版块ID
     */
    private Long id;

    /**
     * 版块名称
     */
    private String name;
    /**
     * 板块头像
     */
    private String image;
    /**
     * 板块背景图片
     */
    private String backgroundImage;
    /**
     * 版块描述
     */
    private String description;

    /**
     * 关注人数
     */
    private Integer followersCount = 0;

    /**
     *帖子数量
     */
    private Integer postCount = 0;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}
