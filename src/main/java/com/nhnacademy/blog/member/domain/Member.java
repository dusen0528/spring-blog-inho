package com.nhnacademy.blog.member.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("java:S107")

/**
 *
 * JPA 엔티티의 조건
 *  - @Entity 애노테이션: 클래스에 @Entity 애노테이션이 있어야 합니다.
 *  - final 사용 금지: 클래스는 final로 선언될 수 없습니다.
 *  - 기본 생성자: public 또는 protected 기본 생성자가 필요합니다.
 *
 * 필드 및 메서드 수준
 *  - 식별자 필드: 하나 이상의 필드에 @Id 애노테이션을 사용하여 식별자를 지정해야 합니다.
 *  - final 사용 금지: 필드와 메서드는 final로 선언될 수 없습니다.
 *  - 접근 제한자: 필드는 private 또는 protected로 선언되고, 접근자 메서드(게터와 세터)를 통해 접근해야 합니다.
 */

@Entity
@Table(name = "members")
public class Member {

    //회원_번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mbNo;
    //회원_이메일

    @Column(nullable = false, unique = true)
    private String mbEmail;
    //회원_이름
    @Column(nullable = false)
    private String mbName;
    //비밀_번호
    @Column(nullable = false)
    private String mbPassword;
    //모바일 연락처
    @Column(nullable = false, unique = true)
    private String mbMobile;
    //생성일(가입일)
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    //수정일
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    //탈퇴일
    private LocalDateTime withdrawalAt;

    public Member() {

    }

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

    public void update(String mbEmail, String mbName, String mbMobile){
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbMobile= mbMobile;
    }

    public void changePassword(String mbPassword){
        this.mbPassword = mbPassword;
    }

    public void withdraw(){
        this.withdrawalAt = LocalDateTime.now();
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
