package com.cyj.xx.WebSocketEventListener;

import com.cyj.xx.Common.UserIdPrincipal;
import com.cyj.xx.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;


@Component
@Slf4j
public class DisConnectListenerEvent implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        UserIdPrincipal principal = (UserIdPrincipal) event.getUser();
        String id = principal.getName();
        String key = "user_Session:"+id;

        if(redisUtil.exists(key)){
            redisUtil.delete(key);
        }
        log.info("用户= => {} <= =已退出聊天",id);

    }
}
