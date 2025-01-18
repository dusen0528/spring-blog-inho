package com.nhnacademy.blog.member.dto.validator;

import com.nhnacademy.blog.member.dto.LoginRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * TODO#2 - LoginRequestValidator를 구현 합니다.
 * - @ComponentScan에 의해서 Bean으로 등록될 수 있도록 설정 합니다.
 */
@Component
public class LoginRequestValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        //TODO#2-1 clazz가 LoginRequest.class인지 검증 합니다. ( 즉 해당 validator는 LoginRequest dto에 대해서 지원됨니다.
        return LoginRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        //TODO#2-2 여기 Object target은 loginRequest 입니다.
        LoginRequest loginRequest = (LoginRequest) target;

        //TODO#2-3 이메일 필드가 비어 있는지 검증 - 아래 코드를 읽고 이해 합니다.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mbEmail", "mbEmail.empty", "이메일은 필수 입력 사항입니다.");

        //TODO#2-4 이메일 형식 검증 - 아래 코드를 읽고 이해 합니다.
        if (loginRequest.getMbEmail() != null && !loginRequest.getMbEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.rejectValue("mbEmail", "mbEmail.invalid", "유효하지 않은 이메일 형식입니다.");
        }

        //TODO#2-5 비밀번호 필드가 비어 있는지 검증 - 아래 코드를 읽고 이해 합니다.
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "mbPassword", "mbPassword.empty", "비밀번호는 필수 입력 사항입니다.");
    }
}
