package com.nhnacademy.blog.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

/**
 *
 * @Email, @NotBlank annotation으 사용하지 않습니다.
 * - 해당 annotation은 주석처리 해주세요
 */
@Value
public class LoginRequest {

    //@Email
    private String mbEmail;

    //@NotBlank
    private String mbPassword;
}