package com.cyj.xx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;


    private Byte type;  // 使用枚举值存储：NotificationType


    private Integer senderId;

    private String senderName;


    private Integer receiverId;


    private Long postId;

    private String title;


    private Long replyId;  // 可为空（点赞帖子时）

    private String content;


    private Boolean isRead = false;



    private LocalDateTime createTime;


}
