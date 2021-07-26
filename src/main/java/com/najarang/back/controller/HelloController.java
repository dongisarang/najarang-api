package com.najarang.back.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

// @Api : 클래스를 Swagger 리소스 대상으로 표시
@Api(tags = {"TestHello"})
@RestController
@RequestMapping("/hello")
public class HelloController {

    private static final String HELLO = "hello";

    @Setter
    @Getter
    private static class Hello {
        private String message;
    }

    // @ApiOperation : 요청 URL 에 매핑된 API 에 대한 설명
    // @ApiParam     : 요청 Parameter에 대한 설명 및 필수여부, 예제값 설정 (ex. UserController)
    // @ApiResponse  : 응답에 대한 설명
    @ApiOperation(value = "테스트 컨트롤러", notes = "hello string 출력")
    @GetMapping(value = "/string")
    public String helloString() {
        return HELLO;
    }

    @ApiOperation(value = "테스트 컨트롤러", notes = "hello json 출력")
    @GetMapping(value = "/json")
    public Hello helloJson() {
        Hello hello = new Hello();
        hello.message = HELLO;
        return hello;
    }
}