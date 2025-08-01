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
public class PostsSectionDVO {
    private Long id;                // 帖子ID
    private String title;           // 帖子标题
    private String content;         // 帖子内容
    private String username;         // 发帖用户name
    private String image = "default_avatar.png";//用户头像URL

    private Integer viewCount;      // 查看次数
    private Integer replyCount;     // 回复数量

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastReplyTime; // 最后回复时间

    private Integer collectCount;     // 收藏数量
    private int isTop;
}
