package com.cyj.xx.controller.user;


import com.cyj.xx.context.BaseContext;
import com.cyj.xx.dto.BatchReadDTO;
import com.cyj.xx.dto.MarkPrivateMsgReadDTO;
import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.InteractionNotificationsService;
import com.cyj.xx.service.PrivateMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/chat")
@Slf4j
public class chatMessageController {

    @Autowired
    PrivateMessageService privateMessageService;

    @Autowired
    InteractionNotificationsService interactionNotificationsService;

    @GetMapping("/private/Contacts")
    public Result privateContacts() {
        return privateMessageService.getContact();
    }


    @GetMapping("/private/Message")
    public Result privateContacts( Long contactId) {
        return privateMessageService.getMessage(contactId);
    }
    @PostMapping("/private/markAsRead")
    public Result privateMarkAsRead(@RequestBody MarkPrivateMsgReadDTO markPrivateMsgReadDTO) {
        return privateMessageService.MarkAsRead(markPrivateMsgReadDTO);
    }

    /**
     * 获取系统信息
     * @return
     */
    @GetMapping("/system/Notifications")
    public Result<List<NotificationDTO>> systemNotifications(Long receiverId) {
        receiverId = BaseContext.getCurrentId().longValue();
        return interactionNotificationsService.getSystemNotifications(receiverId);
    }

    @PatchMapping("/system/mark-read-batch")
    private  Result privateMarkAsRead(@RequestBody BatchReadDTO messageIds) {
        return  interactionNotificationsService.batchMarkAsRead(messageIds.getMessageIds());
    }
}
