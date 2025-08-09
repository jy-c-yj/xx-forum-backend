package com.cyj.xx.Common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Long from; // 发送者ID (字符串)
    private Long to;   // 接收者ID (字符串)
    private String content; // 消息内容
    private String timestamp; // 时间戳 ISO格式
}
