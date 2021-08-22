package com.najarang.back.annotation;

import org.springframework.util.StringUtils;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.util.regex.Pattern;
import java.lang.annotation.ElementType;
import java.lang.annotation.*;

/*
* email validation을 위해 만든 어노테이션
*
* @Target : 어노테이션을 붙일 수 있는 대상 지정
* - FIELD(메소드 필드), CONSTRUCTOR, METHOD, TYPE(클래스, 인터페이스, 열거타입)
* @Constraint : API를 호출할 때 validatedBy로 전달한 validator를 가져오면서 validation을 수행
* @Retention : 어느 시점까지 어노테이션의 메모리를 가져갈 지 설정
* - SOURCE : 어노테이션을 사실상 주석처럼 사용하는 것. 컴파일러가 컴파일할때 해당 어노테이션의 메모리를 버림
* - CLASS : 컴파일러가 컴파일에서는 어노테이션의 메모리를 가져가지만 실질적으로 런타임시에는 사라지게 됨
*           런타임시에 사라진다는 것은 리플렉션으로 선언된 어노테이션 데이터를 가져올 수 없게 됨. 디폴트값
* - RUNTIME : 어노테이션을 런타임시에까지 사용할 수 있음.
*             JVM이 자바 바이트코드가 담긴 class 파일에서 런타임환경을 구성하고 런타임을 종료할 때까지 메모리는 살아있음
* */
@Target(ElementType.FIELD)
@Constraint(validatedBy = Email.EmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {

    String message() default "이메일이 양식에 맞지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default{};

    // 검증 클래스를 만들기 위해서 ConstraintValidator<검증 어노테이션, 필드의 데이터 유형>를 구현
    class EmailValidator implements ConstraintValidator<Email, String> {
        private final String REGEX_EMAIL = "^[\\\\w!#$%&’*+/=?`{|}~^-]+(?:\\\\.[\\\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,6}$";
        public Pattern email = Pattern.compile(REGEX_EMAIL);

        @Override
        public boolean isValid(String s,
                               ConstraintValidatorContext constraintValidatorContext) {
            if(StringUtils.isEmpty(s)) {
                return true;
            } else {
                return email.matcher(s).matches();
            }
        }
    }

}