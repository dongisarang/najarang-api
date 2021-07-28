package com.najarang.back.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
* Redis는 RedisTemplate을 통해서 Redis 서버와 통신함
* - RedisTemplate : Redis module 을 스프링에서 제공함을 통해서 사용자가 좀 더 쉽게 쓸수 있도록 다양한 기능들을 제공
* 높은 수준의 추상화를 통해서, 오퍼레이션들을 제공
*
* ValueOperations : Redis string (or value) operations
* */
@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;

    public String getData(String key){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public void setData(String key, String value){
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key,value);
    }

    public void setDataExpire(String key,String value,long duration){
        // expireDuration기간 동안 저장
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofSeconds(duration);
        valueOperations.set(key,value,expireDuration);
    }

    public void deleteData(String key){
        stringRedisTemplate.delete(key);
    }

}