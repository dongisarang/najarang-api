package com.najarang.back.security;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
* EnableWebSecurity :
*   스프링시큐리티 활성화
*   내부에 @Configuration 있기 때문에 생략 가능
* EnableGlobalMethodSecurity(prePostEnabled = true) :
*   Controller에서 특정 페이지에 특정 권한이 있는 유저만 접근을 허용할 경우
*   @PreAuthorize 어노테이션을 사용하는데,
*   해당 어노테이션에 대한 설정을 활성화 (필수x)
* */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
/*
* CustomAuthenticationProvider를 등록
* => WebSecurityConfigurerAdapter를 상속해 만든 SecurityConfig에서 할 수 있음
* => WebSecurityConfigurerAdapter의 상위 클래스에서는 AuthenticationManager를 가지고 있기 때문에
* 직접 만든 CustomAuthenticationProvider를 등록할 수 있음
*
* AuthenticationManager : 인증에 대한 부분 처리
* => 인증 성공 : 2번째 생성자를 이용해 인증이 성공한(isAuthenticated=true) 객체를 생성하여 Security Context에 저장
* 그리고 인증 상태를 유지하기 위해 세션에 보관
* => 인증 실패 : AuthenticationException 발생
* */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService jwtUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;

    private static final String[] PERMIT_POST_PATHS = {
            "/sign-in",
            "/sign-up",
    };
    private static final String[] PERMIT_GET_PATHS = {
            "/topics",
            "/boards",
            "/comments",
    };

    // @Autowired: 주입 대상이 되는 bean을 컨테이너에 찾아 주입
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // AuthenticationManagerBuilder 를 통해 customSecurityUsersService 에 PasswordEncoder 를 등록
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    // PasswordEncoder는 다른 서비스에서도 쓰일 수 있음으로 빈객체로 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    * WebSecurityConfig 내에서 설정하는 경우는 필요가 없지만 외부에서 인증관리자를 사용하기 위한 설정
    * - 외부로 표출해 주는 메소드를 강제로 호출하여 @Bean으로 등록 ex) 로그인 컨트롤러에서 사용
    * */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // HttpSecurity : 보안 처리
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                // cors 설정
                .cors().configurationSource(corsConfigurationSource()).and()
                // basic auth를 사용하기 위해 csrf 보호 기능 disable
                .csrf().disable()
                // PERMIT_ALL_PATHS에 해당하는 모든 request를 인증
                .authorizeRequests().
                    antMatchers(HttpMethod.POST, PERMIT_POST_PATHS).permitAll().
                    antMatchers(HttpMethod.GET, PERMIT_GET_PATHS).permitAll().
                // 그외 다른요청은 인증 필요
                    anyRequest().authenticated().and().
                        exceptionHandling()
                        // 인증과정에서 실패 401
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        // 필요한 권한이 없이 접근하려 할때 403 에러
                        //.accessDeniedHandler(jwtAccessDeniedHandler)
                .and()
                // JWT 인증에는 기본으로 세션을 사용하지 않기 때문에 stateless를 사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // JWT 인증을 처리할 Filter를 security의 기본적인 필터인 UsernamePasswordAuthenticationFilter 앞에 넣기
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    // WebSecurity : 보안 예외 처리
    // Security filter chain 적용할 필요가 전혀 없는 요청을 ignore하고 싶을 때 사용
    @Override
    public void configure(WebSecurity web) throws Exception {
        // security에 막혀 호출되지 않는 swagger를 위해 uri를 ignoring할 수 있도록 함
        // /v2/api-docs : Spring Fox가 문서를 위해 사용하는 default URL
        web.ignoring().antMatchers(
                "/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/swagger/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }

    // cors 속성
    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("https://localhost:3000");
        configuration.addAllowedOrigin("https://najarang.com");
        configuration.addAllowedHeader("origin");
        configuration.addAllowedHeader("content-type");
        configuration.addAllowedHeader("accept");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}