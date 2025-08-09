package com.cyj.xx.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InteractionNotification {

    public enum NotificationType {
        POST_LIKE(1),    // 点赞帖子
        REPLY_LIKE(2),   // 点赞回复
        POST_REPLY(3),   // 回复帖子
        COMMENT_REPLY(4);// 回复评论

        private final int code;

        NotificationType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }


    private Long id;


    private Byte type;  // 使用枚举值存储：NotificationType


    private Integer senderId;


    private Integer receiverId;


    private Long postId;


    private Long replyId;  // 可为空（点赞帖子时）


    private Boolean isRead = false;


    private Boolean isDeleted = false;


    private LocalDateTime createTime;


    private LocalDateTime readTime;

    // 实用方法：获取通知类型枚举
    public NotificationType getTypeEnum() {
        for (NotificationType t : NotificationType.values()) {
            if (t.getCode() == this.type) {
                return t;
            }
        }
        return null;
    }
}
