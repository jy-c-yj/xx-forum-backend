package com.cyj.xx.config;


import com.cyj.xx.HandshakeHandler.UserIdHandshakeHandler;
import com.cyj.xx.HandshakeHandler.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.addEndpoint("/ws")
                .addInterceptors(webSocketHandshakeInterceptor) // 添加握手拦截器
                .setHandshakeHandler(new UserIdHandshakeHandler())
                .setAllowedOriginPatterns("*");// 允许跨域
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 客户端订阅地址前缀 (PUB/SUB)
        registry.enableSimpleBroker("/topic", "/queue");

        // 客户端发送消息地址前缀 (MessageMapping)
        registry.setApplicationDestinationPrefixes("/app");

        // 点对点消息前缀 (默认/user)
        registry.setUserDestinationPrefix("/user");
    }

}
