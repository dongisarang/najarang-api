package com.najarang.back.service.impl;

import com.najarang.back.advice.exception.CUserNotFoundException;
import com.najarang.back.entity.User;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import com.najarang.back.repo.UserJpaRepo;
import com.najarang.back.service.ResponseService;
import com.najarang.back.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service("userService")
public class UserServiceImpl implements UserService{

    private final UserJpaRepo userJpaRepo;
    private final ResponseService responseService; // 결과를 처리할 Service

    public ListResult<User> findAllUser() {
        return responseService.getListResult(userJpaRepo.findAll());
    }

    public SingleResult<User> findUserById(long id) {
        return responseService.getSingleResult(userJpaRepo.findById(id).orElseThrow(CUserNotFoundException::new));
    }

    public SingleResult<User> save(User user) {
        return responseService.getSingleResult(userJpaRepo.save(user));
    }

    public SingleResult<User> modify(User user) {
        long userId = user.getId();
        // 회원 없으면 예외처리
        User newUser = userJpaRepo.findById(userId).orElseThrow(CUserNotFoundException::new);
        // TODO 닉네임이나 주제 변경있을 경우에만 반영되도록 수정하기
        newUser.setNickname(user.getNickname());
        newUser.setInterestedTopic(user.getInterestedTopic());
        return responseService.getSingleResult(userJpaRepo.save(newUser));
    }

    public CommonResult delete(long id) {
        userJpaRepo.deleteById(id);
        // 성공 결과 정보만 필요한경우 getSuccessResult()를 이용하여 결과를 출력한다.
        return responseService.getSuccessResult();
    }
}