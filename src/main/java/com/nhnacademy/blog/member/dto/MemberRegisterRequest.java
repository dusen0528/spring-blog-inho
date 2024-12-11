package com.nhnacademy.blog.member.dto;

public class MemberRegisterRequest {

    //회원_번호
    private final Long mbNo;
    //회원_이메일
    private final String mbEmail;
    //회원_이름
    private final String mbName;
    //회원_비밀번호
    private final String mbPassword;
    //모바일 연락처
    private final String mbMobile;

    public MemberRegisterRequest(Long mbNo, String mbEmail, String mbName, String mbPassword, String mbMobile) {
        this.mbNo = mbNo;
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbPassword = mbPassword;
        this.mbMobile = mbMobile;
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

    public String getMbPassword() {
        return mbPassword;
    }

    public String getMbMobile() {
        return mbMobile;
    }
}
