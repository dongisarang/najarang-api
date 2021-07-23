package com.najarang.back.security;

import com.najarang.back.advice.exception.CUserNotFoundException;
import com.najarang.back.entity.User;
import com.najarang.back.repo.UserJpaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
* UserDetailsService : Spring Security에서 유저의 정보를 가져오는 인터페이스
*
* @Service : 비즈니스 로직이나 respository layer 호출하는 함수에 사용
* */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserJpaRepo userJpaRepo;
    // NOTE 비밀번호 암호화를 위한 주입. 비밀번호를 적용할 때 사용할 예정
    // private final PasswordEncoder passwordEncoder;

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String[] userInfo = username.split("provider:");

        Optional<User> user = userJpaRepo.findByEmailAndProvider(userInfo[0], userInfo[1]);

        User loginUser = user.orElseThrow(CUserNotFoundException::new);

        return getUserDetails(loginUser);
    }

    private CustomUserDetails getUserDetails(User user) {
        CustomUserDetails userDetail = new CustomUserDetails();
        // String encodedPassword = passwordEncoder.encode(user.getPassword());
        // userDetail.setPassword(encodedPassword);
        userDetail.setID(user.getId().toString());
        userDetail.setEMAIL(user.getEmail());
        userDetail.setPROVIDER(user.getProvider());
        userDetail.setUser(user);
        return userDetail;
    }
}