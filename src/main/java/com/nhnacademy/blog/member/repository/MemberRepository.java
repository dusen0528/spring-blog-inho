package com.nhnacademy.blog.member.repository;

import com.nhnacademy.blog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO#2 - MemberRepository 구현
 */
public interface MemberRepository extends JpaRepository<Member,Long> {

}
