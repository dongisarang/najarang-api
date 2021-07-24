package com.najarang.back.security;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.najarang.back.util.CookieUtil;
import com.najarang.back.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

/*
* Jwt가 유효한 토큰인지 인증하기 위한 Filter
*
* @RequiredArgsConstructor : 
* - 초기화 되지않은 final 필드나 @NonNull 이 붙은 필드에 대해 생성자를 생성
* - 의존성 주입 편의성을 위해서 사용되는 편
*
* Filter : DispatcherServlet 앞에서 먼저 동작
* GenericFilterBean : 기존 Filter에서 얻어올 수 없는 정보였던 Spring의 설정 정보를 가져올 수 있게 확장된 추상 클래스
* => 둘 다 매 서블릿 마다 호출됨
*
* OncePerRequestFilter : 모든 서블릿에 일관된 요청을 처리하기 위해 만들어진 필터
* */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    // 생성자를 이용한 의존성 주입
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    // Request로 들어오는 Jwt 유효성 검증하는 filter를 filterchain에 등록
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final Cookie jwtToken = cookieUtil.getCookie(request, jwtTokenProvider.ACCESS_TOKEN_NAME);

        String username = null;
        String jwt = null;
        String refreshJwt = null;

        try{
            if(jwtToken != null){
                jwt = jwtToken.getValue();
                // username 은 따로 생성한 유니크한 user정보임 > email + "provider:" + provider
                username = jwtTokenProvider.getUsernameFromToken(jwt);
            }

            // username과 인증정보에 대한 null 확인
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = jwtTokenProvider.getAuthentication(username);

                if(jwtTokenProvider.validateToken(jwt, username)){
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch (ExpiredJwtException e){
            // access token이 유효하지 않으면 refresh token 값을 읽음
            refreshJwt = redisUtil.getData(username);
        }catch(Exception e){

        }

        try{
            if(refreshJwt != null){
                username = redisUtil.getData(refreshJwt);

                if(username.equals(jwtTokenProvider.getUsernameFromToken(refreshJwt))){

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = jwtTokenProvider.getAuthentication(username);

                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    Cookie newAccessToken = cookieUtil.createAccessToken(username);

                    response.addCookie(newAccessToken);
                }
            }
        }catch(ExpiredJwtException e){

        }

        chain.doFilter(request, response);
    }

}