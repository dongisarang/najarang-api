package com.najarang.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;


/*
* Spring Data Redis 설정
*
* @EnableRedisRepositories를 통해 레디스 레포지토리를 이용한다고 명시
* */
@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfiguration {

    // @Value : 텍스트 파일로 선언한 변수값(프로퍼티)을 설정
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;


    // RedisConnectionFactory를 통해 내장 혹은 외부의 Redis를 연결
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    // RedisTemplate을 통해 RedisConnection에서 넘겨준 byte 값을 객체 직렬화
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}