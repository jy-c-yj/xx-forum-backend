package com.cyj.xx.entity;

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
public class Posts {
    private Long id;                // 帖子ID
    private String title;           // 帖子标题
    private String content;         // 帖子内容
    private Long userId;         // 发帖用户ID
    private Integer sectionId;      // 所属版块ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime; // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime; // 更新时间
    private Integer viewCount;      // 查看次数
    private Integer replyCount;     // 回复数量
    private LocalDateTime lastReplyTime; // 最后回复时间
    private Boolean isTop;          // 是否置顶(0=否,1=是)
    private Boolean isEssence;      // 是否精华帖(0=否,1=是)
    private Integer status;         // 状态(0=正常,1=删除)
    private Integer collectCount;     // 收藏数量
    private Integer likeCount;       //点赞数量
}
