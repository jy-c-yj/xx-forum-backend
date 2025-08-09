//package com.cyj.xx.interceptor;
//
//import com.cyj.xx.context.BaseContext;
//import com.cyj.xx.properties.JwtProperties;
//import com.cyj.xx.utils.JwtUtil;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Slf4j
//@Component
//public class WebSocketInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    JwtProperties jwtProperties;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("Authorization");
//        String url = request.getRequestURL().toString();
//        log.info("\"拦截器启动\"url:{}", url);
//        if(token == null || token.isEmpty()){
//            response.sendRedirect(request.getContextPath()+"/login");
//        }
//        try{
//            // 移除可能存在的 "Bearer " 前缀
//            if (token.startsWith("Bearer ")) {
//                token = token.substring(7);
//            }
//
//            // 解析 JWT 获取用户 ID
//            Claims claims = JwtUtil.praserJwt(jwtProperties.getSecret(), token);
//            Long userId = Long.valueOf(claims.get("Id").toString());
//            BaseContext.setCurrentId((Long) claims.get("Id"));
//
//
//            System.out.println("Handshake user ID: " + userId + " (type: " + userId.getClass().getSimpleName() + ")");
//            // 将用户 ID 存储在请求属性中
//            request.setAttribute("USER_ID", userId);
//            log.info("webSocket拦截器启动");
//        }catch (Exception e){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Unauthorized: Invalid token");
//            return false;
//
//        }
//
//        return true;
//    }
//}
