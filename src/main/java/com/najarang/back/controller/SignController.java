package com.najarang.back.controller;

import com.najarang.back.dto.SignInRequestDto;
import com.najarang.back.dto.UserDTO;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.security.CustomUserDetails;
import com.najarang.back.security.JwtTokenProvider;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@Slf4j
public class SignController {

    private final UserService userService;
    private final ResponseService responseService; // 결과를 처리할 Service

    @PostMapping(path = "/sign-in")
    public CommonResult signIn(@RequestBody @Valid SignInRequestDto user, HttpServletResponse res) {
        Cookie accessToken = userService.signIn(user);
        res.addCookie(accessToken);
        return responseService.getSuccessResult();
    }

    @PostMapping(path = "/sign-up")
    public CommonResult signUp(@RequestBody UserDTO user, HttpServletResponse res) {
        Cookie accessToken = userService.signUp(user);
        res.addCookie(accessToken);
        return responseService.getSuccessResult();
    }

    @PostMapping(path = "/sign-out")
    public CommonResult signOut(@AuthenticationPrincipal CustomUserDetails customUserDetail, HttpServletResponse res) {
        userService.signOut(customUserDetail.getUser());
        Cookie resetCookie = new Cookie(JwtTokenProvider.ACCESS_TOKEN_NAME, "");
        resetCookie.setMaxAge(0);
        res.addCookie(resetCookie);
        return responseService.getSuccessResult();
    }
}