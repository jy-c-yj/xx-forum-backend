package com.cyj.xx.controller.user;

import com.cyj.xx.Common.Message;
import com.cyj.xx.Common.UserIdPrincipal;
import com.cyj.xx.context.BaseContext;
import com.cyj.xx.entity.PrivateMessage;
import com.cyj.xx.service.PrivateMessageService;
import com.cyj.xx.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Controller
public class MessageController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private  SimpMessagingTemplate brokerMessagingTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PrivateMessageService privateMessageService;

    @MessageMapping("/private-message")
    public void sendPrivateMessage(
            @Payload Message message,
            Principal principal
    ) {
        if (principal instanceof UserIdPrincipal) {
            UserIdPrincipal userPrincipal = (UserIdPrincipal) principal;
            Long senderId = userPrincipal.getUserId();
            message.setFrom(senderId);
            // 构造标准化消息
            Map<String, Object> response = new HashMap<>();
            response.put("from", message.getFrom());
            response.put("to", message.getTo());
            response.put("content", message.getContent());
            response.put("timestamp", Instant.now().toString());

            PrivateMessage privateMessage = new PrivateMessage();
            privateMessage.setMessage(message.getContent());
            privateMessage.setReceiverId(message.getTo());
            privateMessage.setSenderId(message.getFrom());

            privateMessageService.addMessage(privateMessage);
            //判断接收方是否在线
            String key = "user_Session:"+userPrincipal.getName();
            if(redisUtil.exists(key)){
                System.out.println("Sending to user: " + message.getTo());
                brokerMessagingTemplate.convertAndSendToUser(
                        message.getTo().toString(),
                        "/queue/messages",
                        response
                );
            }
        }
    }



}
