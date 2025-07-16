package com.cyj.xx.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 帖子回复实体类
 * 对应数据库表：replies
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Replies {

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
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     * 数据库自动更新，应用层不管理
     */
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 父回复ID
     * 0 = 直接回复帖子
     */

    private Long parentId = 0L;

    /**
     * 状态
     * 0 = 正常, 1 = 删除
     */

    private Integer status = 0;

}
