package com.cyj.xx.dvo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateContactDVO {
    private Long senderId; // 联系人用户ID
    private String name;      // 联系人姓名
    private String avatar;    // 联系人头像URL
    private String lastMessage; // 最后一条消息内容
    private String lastMessageTime; // 最后一条消息时间
    private Integer unreadCount;    //未读消息数
}
