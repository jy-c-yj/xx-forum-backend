package com.cyj.xx.WebSocketEventListener;

import com.cyj.xx.Common.UserIdPrincipal;
import com.cyj.xx.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.SessionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.time.LocalDateTime;

@Component
@Slf4j
public class ConnectListenerEvent implements ApplicationListener<SessionConnectedEvent> {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        UserIdPrincipal principal = (UserIdPrincipal) event.getUser();
        String id = principal.getName();
        String key = "user_Session:"+id;

        if(!redisUtil.exists(key)){
            redisUtil.set(key, LocalDateTime.now());
        }
        log.info("用户= => {} <= =已开始聊天",id);
    }
}
