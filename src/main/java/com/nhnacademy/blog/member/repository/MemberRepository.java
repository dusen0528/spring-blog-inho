package com.nhnacademy.blog.member.repository;

import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository {

    //등록
    void save(Member member);
    //수정
    void update(MemberUpdateRequest memberUpdateRequest);
    //삭제
    void deleteByMbNo(long mbNo);
    //회원 비밀번호 변경
    void updatePassword(long mbNo, String mbPassword);

    //아이디를 이용한 조회
    Optional<Member> findByMbNo(long mbNo);
    //이메일을 이용한 조회
    Optional<Member> findByMbEmail(String mbEmail);
    //모바일 연락처를 이용한 조회
    Optional<Member> findByMbMobile(String mBMobile);

    //회원아이디 존재여부
    boolean existsByMbNo(long mbNo);
    //회원 이메일 존재여부
    boolean existsByMbEmail(String mbEmail);
    //회원 모바일 연락처 존재여부
    boolean existsByMbMobile(String mbMobile);
    //회원 탈퇴시 탈퇴일자 변경
    void updateWithdrawalAt(long mbNo, LocalDateTime updateWithdrawalAt);

}
