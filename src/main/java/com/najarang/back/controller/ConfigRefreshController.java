package com.najarang.back.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/*
* ${spring.profiles}와 같은 사용자 정의 프로퍼티값에 변경이 필요하다면 github에 있는 configuration repo에서 변경 후
* POST로 /actuator/refresh로 요청하면 변경사항이 어플리케이션의 재시작 없이 자동적으로 이루어짐
*
* @RefreshScope : Actuator를 통하여 refresh 할 대상을 설정하는 어노테이션
* - 아래 Controller는 refresh 요청을 받게되면 제거되고 다시 로드될 것임
* */
@RestController
@RefreshScope
public class ConfigRefreshController {

    @Value("${spring.profiles}")
    private String identity;

    @GetMapping("/config/identity")
    public String identity() {
        return identity;
    }
}