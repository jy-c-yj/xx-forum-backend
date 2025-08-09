package com.cyj.xx.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;


@Data
public class PrivateMessage {

    private Integer id; // 消息ID

    private Long senderId; // 发送者

    private Long receiverId; // 接收者


    private String message; // 消息内容


    private String image; // 消息图片URL

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime = LocalDateTime.now(); // 发送时间


    private Boolean isRead = false; // 是否已读


    private Boolean isDeletedSender = false; // 发送者是否删除


    private Boolean isDeletedReceiver = false; // 接收者是否删除
}