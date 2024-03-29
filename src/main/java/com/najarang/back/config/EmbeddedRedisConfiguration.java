package com.najarang.back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/*
* 로컬 환경에서 내장 Redis를 실행하도록 설정
*
* @Profile : 런타임 환경을 설정할 수 있는 기능을 제공
* */
@Profile("local")
@Configuration
public class EmbeddedRedisConfiguration {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    // @PostConstruct : 초기화 작업을 할 메소드에 적용. was가 띄워질 때 실행
    // 생성자(일반)가 호출 되었을 때, 빈(bean)은 아직 초기화 되지 않았다. (예를 들어, 주입된 의존성이 없음)
    // 의존성 주입이 이루어진 후 초기화를 수행.
    // 한번만 수행되는 것을 보장
    @PostConstruct
    public void redisServer() throws IOException {
        redisServer = new RedisServer(redisPort);
        redisServer.start();
    }

    // @PreDestroy: 컨테이너에서 객체를 제거하기 전에 실행
    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }
}