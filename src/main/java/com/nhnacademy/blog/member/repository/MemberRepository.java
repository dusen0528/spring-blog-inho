package com.nhnacademy.blog.member.repository;

import com.nhnacademy.blog.member.domain.Member;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository {

    //등록
    int save(Member member);
    //수정
    int update(Member member);
    //삭제
    int delete(long mbNo);

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
    int updateWithdrawalAt(long mbNo, LocalDateTime updateWithdrawalAt);

}
