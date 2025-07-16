package com.cyj.xx.dvo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotSectionsDVO {
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
     * 关注人数
     */
    private Integer followersCount = 0;

    /**
     *帖子数量
     */
    private Integer postCount = 0;


}
