package com.najarang.back.advice;

import com.najarang.back.advice.exception.CEmailSigninFailedException;
import com.najarang.back.advice.exception.CUserNotFoundException;
import com.najarang.back.model.response.CommonResult;
import com.najarang.back.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
// 예외 발생 시 json형태로 결과를 반환
// annotation에 추가로 패키지를 적용하면 특정 패키지 하위의 Controller에만 로직이 적용되게도 할 수 있음
// ex) @RestControllerAdvice(basePackages = "xxx")
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;

    // Exception이 발생하면 해당 Handler로 처리하겠다고 명시
    @ExceptionHandler(Exception.class)
    // 해당 Exception이 발생하면 Response에 출력되는 HttpStatus Code가 500으로 내려가도록 설정
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult(500, e.getMessage());
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(404, "this user is not exist");
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSigninFailed(HttpServletRequest request, CEmailSigninFailedException e) {
        return responseService.getFailResult(403, "Your account does not exist or your email or password is incorrect.");
    }
}