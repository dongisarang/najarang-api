package com.najarang.back.dto;

import com.najarang.back.annotation.Email;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class SignInRequestDto {
    @Email
    private String email;
    // @NotEmpty : Null을 허용하지 않으며 공백 문자열을 허용하지 않음
    @NotEmpty
    private String provider;
}
