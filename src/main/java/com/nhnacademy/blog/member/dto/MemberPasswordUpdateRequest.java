package com.nhnacademy.blog.member.dto;

public class MemberPasswordUpdateRequest {
    private final Long mbNo;
    private final String oldPassword;
    private final String newPassword;

    public MemberPasswordUpdateRequest(Long mbNo, String oldPassword, String newPassword) {
        this.mbNo = mbNo;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public Long getMbNo() {
        return mbNo;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
