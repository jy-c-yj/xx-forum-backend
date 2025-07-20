package com.cyj.xx.dvo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDVO {
    private String username;

    private String image;

    /**
     * 回复ID
     */

    private Long id;

    /**
     * 回复内容
     */

    private String content;

    /**
     * 回复用户ID
     */

    private Long userId;

    /**
     * 所属帖子ID
     */

    private Long postId;

    /**
     * 点赞数量
     */

    private Integer likeCount = 0;

    /**
     * 创建时间
     * 数据库自动设置默认值，不可更新
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;


    /**
     * 父回复ID
     * 0 = 直接回复帖子
     */

    private Long parentId = 0L;

    /**
     * 子回复数组
     */

    private List<ReplyDVO> children;


}
