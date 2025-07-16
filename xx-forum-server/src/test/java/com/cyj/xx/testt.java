package com.cyj.xx;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
public class testt {
    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void test() {
        redisTemplate.opsForValue().set("aaa","bbb");
    }
}
