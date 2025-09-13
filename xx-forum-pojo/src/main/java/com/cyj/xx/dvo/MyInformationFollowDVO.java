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
public class MyInformationFollowDVO {
    private Long id;                // 板块id
    private String sectionName;     //板块名字
    private Integer grade = 1;             //等级
    private Integer xp = 0;                //经验

}
