package com.najarang.back.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {

    // @ApiModelProperty : 모델의 요소에 설명을 추가 (swagger)
    @ApiModelProperty(value = "응답 코드 번호 : >= 0 정상, < 0 비정상")
    private int code;

    @ApiModelProperty(value = "응답 메시지")
    private String msg;
}