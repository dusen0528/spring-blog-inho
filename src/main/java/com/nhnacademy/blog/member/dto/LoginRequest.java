package com.nhnacademy.blog.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 * TODO#1 - LoginRequest dto를 검증 합니다.
 */
@Value
public class LoginRequest {

    //TODO#1-1 @Email - 유효한 이메일 형식인지 검증
    @Email
    private String mbEmail;

    //TODO#1-2 @NotBlank - 문자열이 null이 아니고, 공백 문자만 포함하지 않는지 검증.
    @NotBlank
    private String mbPassword;
}