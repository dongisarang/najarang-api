package com.najarang.back.controller;

import com.najarang.back.advice.exception.CUnauthorizedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/*
* SpringSecurity를 통해 인증과정에 실패하거나 접근권한이 맞지 않은 경우
* 에러를 처리하기 위한 Controller
* */
@RestController
@RequestMapping("/exception")
public class ExceptionController {

    // 인증 에러
    @GetMapping("/entrypoint")
    public void entrypointException() {
        throw new CUnauthorizedException();
    }

    // 권한 에러
    @GetMapping("/accessdenied")
    public void accessdeniedException() {
        throw new AccessDeniedException("");
    }
}