package com.nhnacademy.blog.member.dto;

import lombok.Value;

@Value
public class LoginRequest {
    private String mbEmail;
    private String mbPassword;
}