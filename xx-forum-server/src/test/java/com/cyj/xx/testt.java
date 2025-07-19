package com.cyj.xx;

import com.alibaba.fastjson.JSON;
import com.cyj.xx.utils.RedisUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class testt {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    RedisTemplate redisTemplate;


    @Test
    public void test() {
        uu u1 = new uu();
        u1.name = "jyc";
        u1.age = 18;
        u1.gendle = 0;
        uu u2 = new uu();
        u2.name = "jy2c";
        u2.age = 128;
        u2.gendle = 20;
//        redisTemplate.opsForHash().
//        uu u = ll.get(0);
//        System.out.println(u.gendle);


    }
}

@Data
class uu{
    String name;
    int age;
    int gendle;
}