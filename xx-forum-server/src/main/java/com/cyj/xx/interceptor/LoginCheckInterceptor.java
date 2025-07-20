package com.cyj.xx.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.cyj.xx.context.BaseContext;
import com.cyj.xx.properties.JwtProperties;
import com.cyj.xx.result.Result;
import com.cyj.xx.utils.JwtUtil;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        log.info("\"拦截器启动\"url:{}", url);

        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }


        String token = request.getHeader("token");
        if(token == null  || "".equals(token)) {
            log.info("token is null");
            if(url.contains("reply") || url.contains("mySec")) {
                return true;
            }
            Result error = Result.error("未登录，请登录");

            if(url.contains("checktoggleLike")){
                error = Result.success(false);
            }
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONObject.toJSONString(error));
            return false;
        }
        //jwt令牌存在
        log.info("进行jwt登陆校验");
        log.info("token:{}", token);
        try {
            Claims claims =  JwtUtil.praserJwt(jwtProperties.getSecret(), token);
            log.info("校验成功");
            Long Id =  Long.valueOf(claims.get("Id").toString());
            log.info("当前userid,{}",Id);
            BaseContext.setCurrentId(Id);
        }catch (Exception e) {
            log.info("登陆失败");
//            Result error = Result.error("登陆信息过期");
//            response.setContentType("application/json;charset=UTF-8");
//            response.getWriter().write(JSONObject.toJSONString(error));
            log.info(url);
            if(url.contains("reply") || url.contains("mySection")) {

                return true;
            }
            return false;

        }
        return true;
    }
}
