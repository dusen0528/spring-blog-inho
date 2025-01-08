package com.nhnacademy.blog.blogmember.repository;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO#5 - BlogMemberMappingRepository 구현
 */
public interface BlogMemberMappingRepository extends JpaRepository<BlogMemberMapping, Long> {

}
