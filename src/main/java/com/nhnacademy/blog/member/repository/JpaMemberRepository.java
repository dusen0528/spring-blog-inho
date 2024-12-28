package com.nhnacademy.blog.member.repository;

import com.nhnacademy.blog.member.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaMemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByMbNo(Long mbNo);
    Optional<Member> findByMbEmail(String mbEmail);
    boolean existsByMbNo(long mbNo);
    boolean existsByMbMobile(String mobile);
    boolean existsByMbNoAndWithdrawalAtIsNotNull(Long mbNo);
    boolean existsByMbEmail(String email);
}
