package com.cyj.xx.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil<T> {
    @Autowired
    private  RedisTemplate redisTemplate;



    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    public boolean delete(String[] key) {
        for (String s : key) {
            redisTemplate.delete(s);
        }
        return true;
    }

    /**
     * 批量获取key
     * @param pattern
     * @return
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }


    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    public Object get(String key) {
        return  redisTemplate.opsForValue().get(key);
    }



    /**
     * 将哈希表key的field字段设置为value
     * @param key
     * @param field
     * @param value
     */
    public  void Hset(String key ,String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 获取哈希表key的field字段的值
     * @param key
     * @param field
     * @return
     */
    public  Object getHash(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取哈希表key的所有字段
     * @param key
     * @return
     */
    public  Object Hkeys(String key) {
        return redisTemplate.opsForHash().keys(key);
    }

    /**
     * 获取哈希表所有的值
     * @param key
     */
    public  Object Hvals(String key) {
       return redisTemplate.opsForHash().values(key);
    }

    /**
     * 保存 Hash 数据并设置过期时间
     *
     * @param key Redis key
     * @param map 要保存的 Map 数据
     * @param timeout 过期时间
     * @param unit 时间单位（秒、分钟、小时等）
     */
    public void hsave(String key, Map<String, Object> map, long timeout, TimeUnit unit) {
        // 1. 保存 Hash 数据
        redisTemplate.opsForHash().putAll(key, map);

        // 2. 设置过期时间
        redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 保存 Hash 数据（使用默认时间单位：秒）
     *
     * @param key Redis key
     * @param map 要保存的 Map 数据
     * @param timeoutSeconds 过期时间（秒）
     */
    public void hsave(String key, Map<String, Object> map, long timeoutSeconds) {
        hsave(key, map, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * 保存 Hash 数据（使用默认过期时间）
     *
     * @param key Redis key
     * @param map 要保存的 Map 数据
     */
    public void hsave(String key, Map<String, Object> map) {
        // 默认过期时间：1小时
        final long DEFAULT_TIMEOUT = 60 * 60;
        hsave(key, map, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
    }


    /**
     *
     */
    public Map hgetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除哈希表key的field字段
     * @param key
     * @param field
     */
    public  void Hdel(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    /**
     * 缓存一个列表
     * @param key
     * @param list
     */

    public void PushAll(String key, List<T> list) {
        redisTemplate.opsForList().leftPushAll(key, list);
    }
    /**
     * 缓存列表（使用默认过期时间）
     * @param key Redis键
     * @param list 要缓存的列表
     */
    public void pushAll(String key, List<T> list) {
        pushAll(key, list, 60, TimeUnit.MINUTES); // 默认1小时
    }

    /**
     * 缓存列表（指定时间单位）
     * @param key Redis键
     * @param list 要缓存的列表
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void pushAll(String key, List<T> list, long timeout, TimeUnit unit) {
        if (list != null && !list.isEmpty()) {
            // 使用右推确保顺序
            redisTemplate.opsForList().rightPushAll(key, list.toArray());

            // 设置过期时间
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
        }
    }

    /**
     * 缓存列表（仅指定秒数）
     * @param key Redis键
     * @param list 要缓存的列表
     * @param timeoutSeconds 过期秒数
     */
    public void pushAll(String key, List<T> list, long timeoutSeconds) {
        pushAll(key, list, timeoutSeconds, TimeUnit.SECONDS);
    }


    /**
     * 刷新列表过期时间
     * @param key Redis键
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void refreshExpire(String key, long timeout, TimeUnit unit) {
        if (timeout > 0) {
            redisTemplate.expire(key, timeout, unit);
        }
    }

    /**
     * 获取列表剩余生存时间
     * @param key Redis键
     * @param unit 时间单位
     * @return 剩余时间
     */
    public long getTimeToLive(String key, TimeUnit unit) {
        Long ttl = redisTemplate.getExpire(key, unit);
        return ttl != null ? ttl : -1;
    }

    /**
     * 追加元素到列表并刷新过期时间
     * @param key Redis键
     * @param element 要添加的元素
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void appendAndRefresh(String key, T element, long timeout, TimeUnit unit) {
        redisTemplate.opsForList().rightPush(key, element);
        refreshExpire(key, timeout, unit);
    }


    /**
     * 获取整个列表
     * @param key
     * @return
     */

    public List GetAll(String key) {
        return (List) redisTemplate.opsForList().range(key, 0, -1);
    }

}
