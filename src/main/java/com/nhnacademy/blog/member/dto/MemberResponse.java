package com.nhnacademy.blog.member.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MemberResponse {

    //회원_번호
    private final Long mbNo;
    //회원_이메일
    private final String mbEmail;
    //회원_이름
    private final String mbName;
    //모바일 연락처
    private final String mbMobile;

    //가입일자
    private final LocalDateTime createdAt;
    //수정일자
    private final LocalDateTime updatedAt;
    /// 탈퇴일자
    private final LocalDateTime withdrawalAt;

}