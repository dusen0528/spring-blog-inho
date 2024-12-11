package com.nhnacademy.blog.member.dto;

import java.time.LocalDateTime;

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
    ///탈퇴일자
    private final LocalDateTime withdrawal_at;


    public MemberResponse(Long mbNo, String mbEmail, String mbName, String mbMobile, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime withdrawal_at) {
        this.mbNo = mbNo;
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbMobile = mbMobile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.withdrawal_at = withdrawal_at;
    }

    public Long getMbNo() {
        return mbNo;
    }

    public String getMbEmail() {
        return mbEmail;
    }

    public String getMbName() {
        return mbName;
    }

    public String getMbMobile() {
        return mbMobile;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getWithdrawal_at() {
        return withdrawal_at;
    }
}
