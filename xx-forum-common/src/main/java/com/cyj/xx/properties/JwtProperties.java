package com.cyj.xx.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "cyj.jwt")
public class JwtProperties {

    private String secret;//密钥
    private Long exptime;//时间
    private String token;

}
