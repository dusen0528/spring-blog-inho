package com.nhnacademy.blog.blogmember.repository;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaBlogMemberMappingRepository extends JpaRepository<BlogMemberMapping, Long> {

    Optional<BlogMemberMapping> findByMbNoAndBlogId(long mbNo, long blogId);
}
