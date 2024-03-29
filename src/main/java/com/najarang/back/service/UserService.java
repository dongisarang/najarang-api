package com.najarang.back.service;

import com.najarang.back.dto.UserDTO;
import com.najarang.back.entity.User;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.model.response.ListResult;
import com.najarang.back.model.response.SingleResult;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.Cookie;

public interface UserService {

    ListResult<User> getUsers(Pageable pageable);
    SingleResult<User> getUser(long id);
    SingleResult<User> save(UserDTO user);
    SingleResult<User> modify(UserDTO user);
    CommonResult delete(long id);
    Cookie signIn(UserDTO user);
    Cookie signUp(UserDTO user);
    void signOut(UserDTO user);
}