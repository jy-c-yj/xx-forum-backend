package com.cyj.xx.config;

import com.cyj.xx.interceptor.LoginCheckInterceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Slf4j
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;



    protected void addInterceptors(InterceptorRegistry registry){
        log.info("拦截器loginCheckInterceptor注册");
        registry.addInterceptor(loginCheckInterceptor)
        .addPathPatterns("/user/**")
        .excludePathPatterns("/user/login")
                .excludePathPatterns("/user/enroll")
                .excludePathPatterns("/user/posts")
                .excludePathPatterns("/user/Secposts")
                .excludePathPatterns("/user/postdetail")
                .excludePathPatterns("/user/addPostView")
                .excludePathPatterns("/user/sections/hot")
                .excludePathPatterns("/user/sections/sectionDetail")
                .excludePathPatterns("/user/checklike")
                .excludePathPatterns("/user/likePost/*")
                .excludePathPatterns("/user//meanUser")
                .excludePathPatterns("/user/post/search")
                .excludePathPatterns("/user/sections/section/search")
                .excludePathPatterns("/user/post/searchInSec");
                //.excludePathPatterns("/user/reply");

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 允许所有路径
                .allowedOrigins("http://localhost:5173") // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS","PATCH") // 允许的方法
                .allowedHeaders("*") // 允许所有头
                .allowCredentials(true) // 允许发送凭证（如cookies）
                .maxAge(3600); // 预检请求的缓存时间（秒）
    }

}
