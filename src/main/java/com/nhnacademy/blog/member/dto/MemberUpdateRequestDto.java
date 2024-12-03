package com.nhnacademy.blog.member.dto;

/**
 * 회원정보 UPDATE Dto
 */
public class MemberUpdateRequestDto {
    //회원_번호
    private final Long mbNo;
    //회원_이메일
    private final String mbEmail;
    //회원_이름
    private final String mbName;
    //비밀_번호
    private final String mbPassword;
    //모바일 연락처
    private final String mbMobile;

    public MemberUpdateRequestDto(Long mbNo, String mbEmail, String mbName, String mbPassword, String mbMobile) {
        this.mbNo = mbNo;
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbPassword = mbPassword;
        this.mbMobile = mbMobile;
    }

    public String getMbMobile() {
        return mbMobile;
    }

    public String getMbPassword() {
        return mbPassword;
    }

    public String getMbName() {
        return mbName;
    }

    public String getMbEmail() {
        return mbEmail;
    }

    public Long getMbNo() {
        return mbNo;
    }

}
