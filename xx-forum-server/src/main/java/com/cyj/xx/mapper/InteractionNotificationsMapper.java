package com.cyj.xx.mapper;


import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.entity.InteractionNotification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InteractionNotificationsMapper {

    List<NotificationDTO> getSystemNotificationsByreceiverId(Long receiverId);

    NotificationDTO getSystemNotificationsById(Long id);

    void updateNotificationById(InteractionNotification interactionNotification);

    void addNotifications(InteractionNotification interactionNotification);
}
