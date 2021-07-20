package com.najarang.back.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
 * http://localhost:8080/swagger-ui.html 로 API를 확인 할 수 있다.
 *
 * @EnableSwagger2 : swagger라이브러리를 추가하여 사용할 수 있게 된 애노테이션
 * */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private String version;
    private String title;

    @Bean
    public Docket apiHello() {
        version = "Hello";
        title = "najarang API " + version;

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false) // 기본으로 세팅되는 200,401,403,404 메시지를 표시 하지 않음
                .groupName(version)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.najarang.back.controller")) // 현재 RequestMapping으로 할당된 URL 리스트를 추출
                .paths(PathSelectors.ant("/helloworld/**")) // 그중 /helloworld/** 인 URL들만 필터링
                .build()
                .apiInfo(apiInfo(title, version));

    }

    @Bean
    public Docket api() {
        version = "v1";
        title = "najarang API " + version;

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.najarang.back.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo(title, version));

    }

    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfoBuilder().title(title)
                .description("앱 개발시 사용되는 서버 API에 대한 연동 문서입니다")
                .license("jalhagosipo").licenseUrl("https://github.com/jalhagosipo").version(version).build();
    }
}