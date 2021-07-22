package com.najarang.back.security;
import java.util.Collection;
import java.util.Collections;

import com.najarang.back.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/*
* Spring Security의 기본 UserDetails로는 필요한 정보를 모두 담을 수 없기에 구현한 CustomUserDetails
*
* UserDetails : Spring Security에서 사용자의 정보를 담는 인터페이스
* */
@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private String ID;
    private String PASSWORD;
    private String EMAIL;
    private String PROVIDER;
    private String AUTHORITY;
    private User user;
    private boolean ENABLED;

    // 계정의 권한 목록을 리턴
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // role 사용 안함
        return Collections.emptyList();
    }

    // 계정의 비밀번호를 리턴
    @Override
    public String getPassword() {
        return PASSWORD;
    }

    // 계정의 고유한 값을 리턴
    @Override
    public String getUsername() {
        return ID;
    }

    // 계정의 만료 여부 리턴 (true : 만료 안됨)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 계정의 잠김 여부 리턴 (true : 잠기지 않음)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 비밀번호 만료 여부 리턴 (true : 만료 안됨)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정의 활성화 여부 리턴 (true : 활성화 됨)
    @Override
    public boolean isEnabled() {
        return ENABLED;
    }
}