package com.najarang.back.controller.v1;

import com.najarang.back.dto.UserDTO;
import com.najarang.back.entity.User;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.security.JwtTokenProvider;
import com.najarang.back.security.JwtUserDetailsService;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.UserService;
import com.najarang.back.util.CookieUtil;
import com.najarang.back.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
@Slf4j
public class SignController {

    private final UserService userService;
    private final ResponseService responseService; // 결과를 처리할 Service
    private final JwtUserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    @PostMapping(path = "/signin")
    public CommonResult signin(@RequestBody UserDTO user, HttpServletRequest req, HttpServletResponse res) {

        try {
            String subject = user.getEmail() + "provider:" + user.getProvider();
            // 올바른 email,provider인지 확인
            userDetailsService.loadUserByUsername(subject);
            final String accessJwt = jwtTokenProvider.generateToken(subject);
            final String refreshJwt = jwtTokenProvider.generateRefreshToken(subject);

            Cookie accessToken = cookieUtil.createCookie(jwtTokenProvider.ACCESS_TOKEN_NAME, accessJwt);
            Cookie refreshToken = cookieUtil.createCookie(jwtTokenProvider.REFRESH_TOKEN_NAME, refreshJwt);
            redisUtil.setDataExpire(refreshJwt, subject, jwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME);

            res.addCookie(accessToken);
            res.addCookie(refreshToken);
            return responseService.getSingleResult(accessJwt);
        } catch (Exception e) {
            return responseService.getFailResult(403, "로그인에 실패했습니다.");
        }
    }

    @PostMapping(path = "/signup")
    public CommonResult signup(@RequestBody UserDTO user) {
        User loginUser = userService.signup(user);
        return responseService.getSuccessResult();
    }
}