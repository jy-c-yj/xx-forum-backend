package com.cyj.xx.service.Impl;

import com.cyj.xx.dto.NotificationDTO;
import com.cyj.xx.entity.InteractionNotification;
import com.cyj.xx.mapper.InteractionNotificationsMapper;
import com.cyj.xx.result.Result;
import com.cyj.xx.service.InteractionNotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class InteractionNotificationsServiceImpl implements InteractionNotificationsService {

    @Autowired
    InteractionNotificationsMapper interactionNotificationsMapper;


    /**
     * 获取用户的所有通知信息
     * @return
     */
    @Override
    public Result<List<NotificationDTO>> getSystemNotifications(Long receiverId) {
        try{
            List<NotificationDTO> ll = interactionNotificationsMapper.getSystemNotificationsByreceiverId(receiverId);

            return Result.success(ll);
        }catch (Exception e){
            return Result.error("初始化通知失败");
        }


    }

    @Override
    public Result batchMarkAsRead(List<Long> messageIds) {

        List<InteractionNotification> ll = new ArrayList<InteractionNotification>();
        for(Long id : messageIds){
            InteractionNotification notification = new InteractionNotification();
            notification.setId(id);
            notification.setIsRead(true);
            ll.add(notification);
        }
        try{
            for(InteractionNotification notification : ll){
                interactionNotificationsMapper.updateNotificationById(notification);
            }

            return Result.success();
        }catch (Exception e){}
        return Result.success("修改read状态失败");
    }
}
