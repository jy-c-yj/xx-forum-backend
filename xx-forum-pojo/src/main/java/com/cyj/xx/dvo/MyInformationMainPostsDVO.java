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
public class MyInformationMainPostsDVO {
    private Long id;                // 帖子ID
    private String title;           // 帖子标题
    private String content;         // 帖子内容
    private Long userId;         // 发帖用户ID
    private String userName;     // 发帖用户名
    private Integer sectionId;      // 所属版块ID
    private String sectionName;     // 所属板块名
    private String sectionImage;    // 所属板块头像
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 创建时间
    private Integer replyCount;     // 回复数量

}
