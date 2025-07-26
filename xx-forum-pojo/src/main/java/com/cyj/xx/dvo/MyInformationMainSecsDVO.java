package com.cyj.xx.dvo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyInformationMainSecsDVO {
    /**
     * 版块ID
     */
    private Long id;

    /**
     * 版块名称
     */
    private String sectionName;
    /**
     * 板块头像
     */
}
