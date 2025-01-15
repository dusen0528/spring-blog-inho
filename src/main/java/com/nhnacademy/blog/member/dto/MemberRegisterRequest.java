package com.nhnacademy.blog.member.dto;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class MemberRegisterRequest {
    //회원_이메일
    private final String mbEmail;
    //회원_이름
    private final String mbName;
    //회원_비밀번호
    private final String mbPassword;
    //회원_비밀번호 확인
    private final String mbPasswordConfirm;
    //모바일 연락처
    private final String mbMobile;

}
