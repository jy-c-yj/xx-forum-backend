package com.cyj.xx.service;

import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.result.Result;

import java.util.List;

public interface InteractionNotificationsService {


    Result<List<NotificationDTO>> getSystemNotifications(Long receiverId);



    Result batchMarkAsRead(List<Long> messageIds);
}
