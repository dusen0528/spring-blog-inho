package com.nhnacademy.blog.blogmember.repository;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogMemberMappingRepository extends JpaRepository<BlogMemberMapping, Long> {

}
