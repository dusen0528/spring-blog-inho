package com.nhnacademy.blog.member.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("java:S107")

/** Member Entity
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
//@Entity anootation을 선언하세요.
@Entity
//@Table annotation을 사용하여 TableName을 지정합니다.
@Table(name = "members")
public class Member {

    //@Id annotation을 사용하여 entity를 식별할 수 있는 ID Field를 지정합니다.
    //회원_번호
    @Id

    //@GeneratedValue 를 사용하여 ID생성 전략을 설정 합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mb_no")
    private Long mbNo;

    //회원_이메일
    /*mbEmail의 @Column annotation을 참고하여 나머지 필드를 수정 합니다.
    *  - name = 'mb_email' 맵핑할 column name입니다.
    *    db 컬럼(스네이크 표현식) -> entity(카멜표현식) 자동 맵핑 됩니다.
    *    ex) mb_email -> mbEmail , 이럴때는 name을 생략할 수 있습니다.
    *  - nullable : not null 여부 설정,
    *  - unique  : unique 제약조건 설정
    *  - length : 100 <-- varcahr(100), 즉 컬럼의 길이 설정
    *
    *  가능한 이유는
    *  @see JpaConfig.java
    *  의 hibernate.physical_naming_strategy 설정을 참고하세요.
    * */
    @Column(name="mb_email",nullable = false, unique = true, length = 100)
    private String mbEmail;

    //@Column annotation 선언
    //회원_이름
    @Column(nullable = false, length = 50)
    private String mbName;

    //@Column annotation 선언
    //비밀_번호
    @Column(nullable = false, length = 200)
    private String mbPassword;

    //@Column annotation 선언
    //모바일 연락처
    @Column(nullable = false, unique = true, length = 100)
    private String mbMobile;

    //@Column annotation 선언
    //생성일(가입일)
    @CreationTimestamp // entity 생성시 날짜가 자동 설정 됩니다.
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //@Column annotation 선언
    //수정일
    private LocalDateTime updatedAt;

    //@Column annotation 선언
    //탈퇴일
    private LocalDateTime withdrawalAt;

    //entity는 반드시 기본생성자가 있어야 합니다.
    public Member() {

    }

    //엔티티가 영속화 되기전(@PrePersist)에 null로 추기화 합니다.
    @PrePersist
    public void prePersistForUpdatedAt() {
        this.updatedAt = null;
    }

    //entity가 업데이트 하기전(@PreUpdate)에 updateAt = 현재시간으로 설정 합니다.
    @PreUpdate
    public void preUpdateForUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
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

    //member정보를 업데이트 하는 method를 구현 합니다.
    public void update(String mbEmail, String mbName, String mbMobile){
        this.mbEmail = mbEmail;
        this.mbName = mbName;
        this.mbMobile= mbMobile;
    }

    //비밀번호를 수정하는 하는 method를 구현 합니다.
    public void changePassword(String mbPassword){
        this.mbPassword = mbPassword;
    }

    //회원탈퇴하는 method를 구현합니다.,withdrawalAt(탈퇴일자)를 현재 시간으로 설정 합니다.
    public void withdraw(){
        this.withdrawalAt = LocalDateTime.now();
    }

    //해당 method를 참고하여 getter Method를 구현합니다.
    //될 수 있으면 Setter Method는 사용하지 않습니다.

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

    //equals(),  hashCode(), toString() method를 구현 합니다.
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
