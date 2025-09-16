package com.cyj.xx.dvo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 封禁用户信息 DVO (Data View Object)
 * 用于向前端返回封禁用户信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BanPostLogDVO {
    /**
     * 封禁记录ID
     */
    private Integer id;

    /**
     * 帖子id
     */
    private String postId;
    /**
     * 帖子标题
     */
    private String title;

    /**
     * 所属板块名称
     */
    private String sectionName;

    /**
     * 封禁原因
     */
    private String banReason;

    /**
     * 封禁时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


}