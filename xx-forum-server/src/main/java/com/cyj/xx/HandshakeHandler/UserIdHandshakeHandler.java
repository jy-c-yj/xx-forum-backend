package com.cyj.xx.HandshakeHandler;

import com.cyj.xx.Common.UserIdPrincipal;
import com.cyj.xx.properties.JwtProperties;
import com.cyj.xx.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;


@Slf4j
public class UserIdHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // 从握手拦截器设置的属性中获取用户 ID
        Long userId = (Long) attributes.get("USER_ID");


        if (userId != null) {
            log.info("创建 Principal 用户 ID: {}", userId);
            return new UserIdPrincipal(userId);
        }

        return null; // 返回 null 会拒绝连接
    }
}
