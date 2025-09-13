package com.cyj.xx.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class UpdataSectionDTO {
    /**
     * 版块ID
     */
    private Long id;

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

}
