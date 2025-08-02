package com.cyj.xx.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSectionDTO {
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

}
