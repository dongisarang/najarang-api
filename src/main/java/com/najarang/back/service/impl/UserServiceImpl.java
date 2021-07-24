package com.najarang.back.service.impl;

import com.najarang.back.advice.exception.CUserAlreadyExistException;
import com.najarang.back.advice.exception.CUserNotFoundException;
import com.najarang.back.dto.UserDTO;
import com.najarang.back.entity.User;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.repo.UserJpaRepo;
import com.najarang.back.security.JwtTokenProvider;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.UserService;
import com.najarang.back.util.CookieUtil;
import com.najarang.back.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.util.Optional;

@RequiredArgsConstructor
@Service("userService")
@Slf4j
public class UserServiceImpl implements UserService{

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final RedisUtil redisUtil;

    public ListResult<User> getUsers(Pageable pageable) {
        Page<User> users = userJpaRepo.findAll(pageable);
        return responseService.getListResult(users);
    }

    public SingleResult<User> getUser(long id) {
        return responseService.getSingleResult(userJpaRepo.findById(id).orElseThrow(CUserNotFoundException::new));
    }

    public SingleResult<User> save(UserDTO user) {
        return responseService.getSingleResult(userJpaRepo.save(user.toEntity()));
    }

    public SingleResult<User> modify(UserDTO user) {
        long userId = user.getId();
        // 회원 없으면 예외처리
        User newUser = userJpaRepo.findById(userId).orElseThrow(CUserNotFoundException::new);
        UserDTO userDto = newUser.toDTO();
        if (user.getNickname() != null) userDto.setNickname(user.getNickname());
        if (user.getInterestedTopic() != null) userDto.setInterestedTopic(user.getInterestedTopic());
        return responseService.getSingleResult(userJpaRepo.save(userDto.toEntity()));
    }

    public CommonResult delete(long id) {
        userJpaRepo.deleteById(id);
        // 성공 결과 정보만 필요한경우 getSuccessResult()를 이용하여 결과를 출력한다.
        return responseService.getSuccessResult();
    }

    public Cookie signIn(UserDTO user) {
        Optional<User> loginUser = userJpaRepo.findByEmailAndProvider(user.getEmail(), user.getProvider());
        loginUser.orElseThrow(CUserNotFoundException::new);
        Cookie accessToken = this.loginSuccess(loginUser.get());
        return accessToken;
    }

    public Cookie signUp(UserDTO user) {
        Optional<User> loginUser = userJpaRepo.findByEmailAndProvider(user.getEmail(), user.getProvider());
        loginUser.ifPresent(s -> { throw new CUserAlreadyExistException(); });
        User insertedUser = userJpaRepo.save(user.toEntity());
        Cookie accessToken = this.loginSuccess(insertedUser);
        return accessToken;
    }

    public void signOut(UserDTO user) {
        String subject = jwtTokenProvider.makeStringUserDetails(user.toEntity());
        redisUtil.deleteData(subject);
    }

    private Cookie loginSuccess(User user) {
        String subject = jwtTokenProvider.makeStringUserDetails(user);
        Cookie accessToken = cookieUtil.createAccessToken(subject);
        String refreshToken = jwtTokenProvider.generateRefreshToken(subject);
        redisUtil.setDataExpire(subject, refreshToken, JwtTokenProvider.REFRESH_TOKEN_EXPIRE_TIME);
        return accessToken;
    }
}