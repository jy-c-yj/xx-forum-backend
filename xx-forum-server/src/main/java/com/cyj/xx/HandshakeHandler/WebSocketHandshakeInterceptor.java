package com.cyj.xx.HandshakeHandler;

import com.cyj.xx.properties.JwtProperties;
import com.cyj.xx.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("WebSocket 握手拦截器被触发");

        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 从查询参数获取 token
            String token = httpRequest.getParameter("token");

            if (token == null || token.isEmpty()) {
                log.warn("WebSocket 连接缺少 token");
                return false; // 拒绝握手
            }

            try {
                // 解码 URL 编码的 token
                token = URLDecoder.decode(token, StandardCharsets.UTF_8.name());

                log.info("获取到 token: {}", token);

                // 解析 JWT
                Claims claims = JwtUtil.praserJwt(jwtProperties.getSecret(), token);
                Long userId = Long.valueOf(claims.get("Id").toString());

                log.info("验证用户 ID: {}", userId);

                // 将用户 ID 存储在会话属性中
                attributes.put("USER_ID", userId);

                return true; // 允许握手
            } catch (Exception e) {
                log.error("JWT 解析失败: {}", e.getMessage());
                return false; // 拒绝握手
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后处理（可选）
    }
}
