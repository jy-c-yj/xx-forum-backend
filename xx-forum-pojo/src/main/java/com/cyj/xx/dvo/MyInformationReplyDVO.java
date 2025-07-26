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
public class MyInformationReplyDVO {
    private Long id;                // 回复id
    private String content;         //回复内容
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;//回复时间
    private Long postId;             //回复帖子id
    private String title;            //回复帖子标题
    private Integer replyCount;     // 回复帖子的回复数量
    private Long sectionId;         //回复帖子板块
    private String sectionName;     //板块名字
}
