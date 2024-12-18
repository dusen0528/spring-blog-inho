package com.nhnacademy.blog.member.dto;

public class LoginMember {
    //회원번호
    private final long mbNo;
    //이메일
    private final String mbEmail;
    //이름
    private final String mbName;

    public LoginMember(long mbNo, String mbEmail, String mbName) {
        this.mbNo = mbNo;
        this.mbEmail = mbEmail;
        this.mbName = mbName;
    }

    public long getMbNo() {
        return mbNo;
    }

    public String getMbEmail() {
        return mbEmail;
    }

    public String getMbName() {
        return mbName;
    }

    @Override
    public String toString() {
        return "LoginMember{" +
                "mbNo=" + mbNo +
                ", mbEmail='" + mbEmail + '\'' +
                ", mbName='" + mbName + '\'' +
                '}';
    }
}
