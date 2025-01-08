package com.nhnacademy.blog.member.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;

/**
 * TODO#1 - members entity mapping
 * erd : https://www.erdcloud.com/d/Q8FBdJLcNApqBp5mt 참고하여 entity mapping을 진행 합니다.
 * - Member Entity는 예시로 제공해 드립니다.
 * - Member Entity를 기반으로 나머지 Entity를 매핑 하세요.
 */
@Entity
@Table(name = "members")

/**
 *  public Member(){}  기본생성자를 생성해주는 역할을 합니다.
 *  - 엔티티 클래스의 인스턴스가 외부에서 무분별하게 생성되지 않도록 하기 위해서 protected로 access level을 설정 합니다.
 *  - procted Member(){} <-- 접근 제한자가 protected로 생성됨
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)

/**
 * public Long getMbNo(){
 *  return this.mbNo
 * } <-- 형태의 getter method를 자동 생성 합니다.
 *
 * - @Setter annotation을 사용하면 아래와 같은 method를 자동생성 합니다.
 * public void setMbNo(Long mbNo){
 *     this.mbNo = mbNo
 * }
 *
 * - @Setter annotation은 무분별하게 사용하지 않습니다.
 * - 반드시 필요한 곳에 사용하세요.
 */
@Getter

/**
 *  - 아래와 같은 toString method를 자동 생성 합니다.
 *     @Override
 *     public String toString() {
 *         return "Member{" +
 *                 "mbNo=" + mbNo +
 *                 ", mbEmail='" + mbEmail + '\'' +
 *                 ", mbName='" + mbName + '\'' +
 *                 ", mbPassword='" + mbPassword + '\'' +
 *                 ", mbMobile='" + mbMobile + '\'' +
 *                 ", createdAt=" + createdAt +
 *                 ", updatedAt=" + updatedAt +
 *                 ", withdrawalAt=" + withdrawalAt +
 *                 '}';
 *     }
 *
 */
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mb_no")
    @Comment("회원-번호") //데이터베이스 컬럼의 주석을 표현할 때 사용하는 annotation
    private Long mbNo;

    @Column(name="mb_email",nullable = false, unique = true, length = 100)
    @Comment("회원-이메일")
    private String mbEmail;

    @Column(nullable = false, length = 50)
    @Comment("회원-이름")
    private String mbName;

    @Column(nullable = false, length = 200)
    @Comment("비밀번호")
    private String mbPassword;

    @Column(nullable = false, unique = true, length = 100)
    @Comment("전화번호")
    private String mbMobile;

    @Column(nullable = false, updatable = false)
    @Comment("가입일자")
    private LocalDateTime createdAt;

    @Comment("수정일자")
    private LocalDateTime updatedAt;

    @Comment("탈퇴일자")
    private LocalDateTime withdrawalAt;


    private Member(String mbEmail, String mbName, String mbPassword, String mbMobile){
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbPassword = mbPassword;
        this.mbMobile = mbMobile;
    }

    public static Member ofNewMember(String mbEmail, String mbName, String mbPassword, String mbMobile){
        return new Member(mbEmail, mbName, mbPassword, mbMobile);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * setter method의 사용을 최소화 하기 위해서 , 회원을 수정한다는 의미의 update 메서드를 추가 했습니다.
     * member정보를 업데이트 하는 method를 구현 합니다
     */
    public void update(String mbEmail, String mbName, String mbMobile){
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbMobile= mbMobile;
    }

    /**
     *  setMbPassword 같은 setter method를 사용하기 보다는 changePassword<-- 의미를 부여하여 method 작성
     */
    public void changePassword(String mbPassword){
        this.mbPassword = mbPassword;
    }

    /**
     *  - setter method를 사용하지 않고 회원 탈퇴라는 의미의 method를 사용함.
     * 회원탈퇴하는 method를 구현합니다.,withdrawalAt(탈퇴일자)를 현재 시간으로 설정 합니다.
     */
    public void withdraw(){
        this.withdrawalAt = LocalDateTime.now();
    }

}
