package com.najarang.back.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
/*
* CustomAuthenticationProvider를 등록
* => WebSecurityConfigurerAdapter를 상속해 만든 SecurityConfig에서 할 수 있음
* => WebSecurityConfigurerAdapter의 상위 클래스에서는 AuthenticationManager를 가지고 있기 때문에
* 우리가 직접 만든 CustomAuthenticationProvider를 등록할 수 있음
*
* AuthenticationManager : 인증에 대한 부분 처리
* => 인증 성공 : 2번째 생성자를 이용해 인증이 성공한(isAuthenticated=true) 객체를 생성하여 Security Context에 저장
* 그리고 인증 상태를 유지하기 위해 세션에 보관
* => 인증 실패 : AuthenticationException 발생
* */
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 주입 대상이 되는 bean을 컨테이너에 찾아 주입
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService jwtUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    private static final String[] PERMIT_ALL_PATHS = {
            "/signin",
            "/signup",
            "/topics",
            "/boards",
            "/board/**",
            "/helloworld/**",
            "/error/**"
    };

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // configure AuthenticationManager so that it knows from where to load
        // user for matching credentials
        // Use BCryptPasswordEncoder
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                // dont authenticate this particular request
                .authorizeRequests().
                    antMatchers(PERMIT_ALL_PATHS).permitAll().
                // all other requests need to be authenticated
                    anyRequest().authenticated().and().
                        exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                // JWT 인증에는 기본으로 세션을 사용하지 않기 때문에 stateless를 사용
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Add a filter to validate the tokens with every request
        // JWT 인증을 처리할 Filter를 security의 기본적인 필터인 UsernamePasswordAuthenticationFilter 앞에 넣기
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}