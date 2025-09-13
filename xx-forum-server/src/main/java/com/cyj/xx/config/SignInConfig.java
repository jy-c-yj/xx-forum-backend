package com.cyj.xx.config;


import com.cyj.xx.QuatzJob.UserSignInJob;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignInConfig {
    @Bean
    public UserSignInJob UserSignInJob(){
        return new UserSignInJob();
    }
}
