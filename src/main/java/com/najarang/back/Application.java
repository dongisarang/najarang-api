package com.najarang.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/*
* EnableJpaAuditing : JPA Auditing을 활성화
* SpringBootApplication : 가장 기본적인 설정을 선언. 아래의 3가지 역할 수행
* => SpringBootConfiguration :
*   스프링의 @Configuration을 대체하며 스프링 부트 전용 어노테이션
* => ComponentScan :
*   @component 어노테이션 및 @Service, @Repository, @Controller 등의 어노테이션을 스캔하여 Bean으로 컨테이너에 등록
* => EnableAutoConfiguration :
*   사전에 정의한 라이브러리들을  Bean으로 등록
*   @ConditionalOnXxx… 와 같은 형태로 Condition에 적합한 경우 생성하고 생성하지 않게 설정
* */
@EnableJpaAuditing
@SpringBootApplication
// 외부 Tomcat에서 동작하도록 하기위한 배포 방식인 war 파일로 배포를 진행해야 하는 경우
// web.xml(or WebApplicationInitializer)에 애플리케이션 컨텍스트를 등록
// ->Tomcat(Servlet Container)이 구동될 때 /WEB-INF 디렉토리에 존재하는 web.xml을 읽어 웹 애플리케이션을 구성하기 때문
// public class Application extends SpringBootServletInitializer {
// 서버를 내장하여 배포하는 jar파일인 경우
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
