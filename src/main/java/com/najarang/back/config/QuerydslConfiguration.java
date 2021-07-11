package com.najarang.back.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// 설정 클래스를 선언하는 어노테이션
// @Configuration + @Bean
// 외부라이브러 또는 내장 클래스를 bean으로 등록하고자 할 경우 사용
// 1개 이상의 @Bean을 제공하는 클래스의 경우 반드시 @Configuraton을 명시
@Configuration
public class QuerydslConfiguration {

    @PersistenceContext
    private EntityManager entityManager;

    // bean을 정의하는 어노테이션
    @Bean
    public JPAQueryFactory jpaQueryFactory(){ return new JPAQueryFactory(entityManager); }
}