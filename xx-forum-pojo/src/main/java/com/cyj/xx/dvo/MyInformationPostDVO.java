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
public class MyInformationPostDVO {
    private Long id;                // 帖子id
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;//发帖时间时间
    private String title;            //帖子标题
    private Integer replyCount;     // 帖子的回复数量
    private Long sectionId;         //帖子板块
    private String sectionName;     //板块名字
}
