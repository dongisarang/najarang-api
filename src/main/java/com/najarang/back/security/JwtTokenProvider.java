package com.najarang.back.security;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.najarang.back.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
/*
* JwtTokenProvider => jwt 토큰 생성 및 검증
* 참고 : https://www.javainuse.com/spring/boot-jwt
*
* @Bean : 개발자가 컨트롤이 불가능한 외부 라이브러리들을 Bean으로 등록하고 싶은 경우에 사용
* @Component: 개발자가 직접 작성한 클래스를 bean 등록하고자 할 경우 사용
* */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    // 토큰 유효 시간
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 7;  // 7일
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 5 * 60 * 60; // 5분

    public static final String ACCESS_TOKEN_NAME = "accessToken";
    public static final String REFRESH_TOKEN_NAME = "refreshToken";

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Value("${security.jwt.token.secret-key}")
    private String secret;

    // jwt 토큰으로부터 username(CustomUserDetails의 ID를 말함.unique) 가져오기
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // jwt token의 만료날짜 가져오기
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Function<Claims, T> claimsResolver : Claims 타입의 인자를 받고, T타입의 객체 리턴하는 함수형 인터페이스
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    // jwt token에서 정보가져오기
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    // 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // 액세스 토큰 생성
    public String generateToken(String userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails, ACCESS_TOKEN_EXPIRE_TIME);
    }

    // 리프레시 액세스 토큰 생성
    public String generateRefreshToken(String userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails, REFRESH_TOKEN_EXPIRE_TIME);
    }

    // 토큰 생성
    // 1. Define  claims of the token, like Issuer, Expiration, Subject, and the ID
    // 2. Sign the JWT using the HS512 algorithm and secret key.
    // 3. According to JWS Compact Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
    //   compaction of the JWT to a URL-safe string
    private String doGenerateToken(Map<String, Object> claims, String subject, long expireTime) {

        return Jwts.builder()
                .setClaims(claims) // claim 넣기 - 사용자에 대한 프로퍼티/속성. 회원을 구분할 수 있는 값을 세팅
                .setSubject(subject) // token 제목
                .setIssuedAt(new Date(System.currentTimeMillis())) // token 생성 날짜
                .setExpiration(new Date(System.currentTimeMillis() + expireTime * 1000)) // token 유효시간
                .signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    // 토큰 검증
    public Boolean validateToken(String token, String userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails) && !isTokenExpired(token));
    }

    // jwt 토큰으로 인증 정보를 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String username) {
        CustomUserDetails userDetails = this.userDetailsServiceImpl.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails,null, userDetails.getAuthorities());
    }

    public String makeStringUserDetails(User user) {
        return user.getEmail() + "provider:" + user.getProvider();
    }
}