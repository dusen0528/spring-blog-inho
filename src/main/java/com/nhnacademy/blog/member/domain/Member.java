package com.nhnacademy.blog.member.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Member {

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
    //생성일(가입일)
    private final LocalDateTime createdAt;
    //수정일
    private final LocalDateTime updatedAt;
    //탈퇴일
    private final LocalDateTime withdrawalAt;

    private Member(Long mbNo, String mbEmail, String mbName, String mbPassword, String mbMobile, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime withdrawalAt) {
        this.mbNo = mbNo;
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbPassword = mbPassword;
        this.mbMobile = mbMobile;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.withdrawalAt = withdrawalAt;
    }

    public static Member ofNewMember(String mbEmail, String mbName, String mbPassword, String mbMobile) {
        return new Member(null, mbEmail, mbName, mbPassword, mbMobile, null, null, null);
    }

    public static Member ofExistingMember(Long mbNo, String mbEmail, String mbName, String mbPassword, String mbMobile,LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime withdrawalAt) {
        return new Member(mbNo, mbEmail, mbName, mbPassword, mbMobile, createdAt, updatedAt, withdrawalAt);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getWithdrawalAt() {
        return withdrawalAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(mbNo, member.mbNo) && Objects.equals(mbEmail, member.mbEmail) && Objects.equals(mbName, member.mbName) && Objects.equals(mbPassword, member.mbPassword) && Objects.equals(mbMobile, member.mbMobile) && Objects.equals(createdAt, member.createdAt) && Objects.equals(updatedAt, member.updatedAt) && Objects.equals(withdrawalAt, member.withdrawalAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mbNo, mbEmail, mbName, mbPassword, mbMobile, createdAt, updatedAt, withdrawalAt);
    }

    @Override
    public String toString() {
        return "Member{" +
                "mbNo=" + mbNo +
                ", mbEmail='" + mbEmail + '\'' +
                ", mbName='" + mbName + '\'' +
                ", mbPassword='" + mbPassword + '\'' +
                ", mbMobile='" + mbMobile + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", withdrawalAt=" + withdrawalAt +
                '}';
    }
}
