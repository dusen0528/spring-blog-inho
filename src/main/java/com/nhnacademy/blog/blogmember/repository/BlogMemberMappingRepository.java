package com.nhnacademy.blog.blogmember.repository;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;

import java.util.Optional;

public interface BlogMemberMappingRepository {

    void save(BlogMemberMapping blogMemberMapping);
    void deleteByBlogMemberMappingId(Long blogMemberId);
    Optional<BlogMemberMapping> findByBlogMemberId(Long blogMemberId);
    Optional<BlogMemberMapping> findByMbNoAndBlogId(Long mbNo, Long blogId);
}