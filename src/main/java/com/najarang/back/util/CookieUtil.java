package com.najarang.back.util;

import com.najarang.back.security.JwtTokenProvider;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 토큰을 쿠키 형태로 저장하기 위해 사용하는 클래스
 */
@Service
public class CookieUtil {

    public Cookie createCookie(String cookieName, String value){
        int expireTime = (int) JwtTokenProvider.ACCESS_TOKEN_EXPIRE_TIME;
        if(cookieName.equals(JwtTokenProvider.REFRESH_TOKEN_NAME)) {
            expireTime = (int) JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME;
        }
        Cookie token = new Cookie(cookieName,value);
        token.setHttpOnly(true); // 쿠키는 클라이언트 측 스크립트에 접근x. 악성 스크립트(XSS 공격)에 의한 위험 완화
        token.setMaxAge(expireTime); // 쿠키 유효기간 설정
        token.setPath("/"); // 모든 경로에서 접근 가능하도록 설정
        return token;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

}