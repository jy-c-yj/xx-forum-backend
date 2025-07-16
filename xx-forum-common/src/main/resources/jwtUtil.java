package com.cyj.xx.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;

public class jwtUtil {

    public static String createJwt(String key, long time, HashMap<String,Object> map) {
        //生效时间
        Date exp = new Date(System.currentTimeMillis() + time);

        HashMap<String, Object> c = new HashMap();
        c.put("key", key);

        //生成JWT
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256,key.getBytes(StandardCharsets.UTF_8))
                .setClaims(map)
                .setClaims(c)
                .setExpiration(exp);

        return jwtBuilder.compact();

    }

    public static Claims praserJwt(String key,String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token).getBody();

        return claims;

    }


}
