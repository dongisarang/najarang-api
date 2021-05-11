package com.najarang.back.auth;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final Cookie jwtToken = cookieUtil.getCookie(request, jwtTokenUtil.ACCESS_TOKEN_NAME);

        String username = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshUname = null;

        try{
            if(jwtToken != null){
                jwt = jwtToken.getValue();
                // username 은 따로 생성한 유니크한 user정보임 > email + "provider:" + provider
                username = jwtTokenUtil.getUsernameFromToken(jwt);
            }
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                CustomUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);
                String subject = userDetails.getEMAIL() + "provider:" + userDetails.getPROVIDER();

                if(jwtTokenUtil.validateToken(jwt, subject)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }catch (ExpiredJwtException e){
            // access token이 유효하지 않으면 refresh token 값을 읽음
            Cookie refreshToken = cookieUtil.getCookie(request, jwtTokenUtil.REFRESH_TOKEN_NAME);
            if(refreshToken != null){
                refreshJwt = refreshToken.getValue();
            }
        }catch(Exception e){

        }

        try{
            if(refreshJwt != null){
                refreshUname = redisUtil.getData(refreshJwt);

                if(refreshUname.equals(jwtTokenUtil.getUsernameFromToken(refreshJwt))){
                    CustomUserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(refreshUname);
                    String subject = userDetails.getEMAIL() + "provider:" + userDetails.getPROVIDER();

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    final String newAccessJwt = jwtTokenUtil.generateToken(subject);

                    Cookie newAccessToken = cookieUtil.createCookie(jwtTokenUtil.ACCESS_TOKEN_NAME, newAccessJwt);

                    response.addCookie(newAccessToken);
                }
            }
        }catch(ExpiredJwtException e){

        }

        chain.doFilter(request, response);
    }

}