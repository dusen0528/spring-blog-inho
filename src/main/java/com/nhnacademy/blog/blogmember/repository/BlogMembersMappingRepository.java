package com.nhnacademy.blog.blogmember.repository;

import com.nhnacademy.blog.blogmember.domain.BlogMembersMapping;

import java.util.Optional;

public interface BlogMembersMappingRepository {

    void save(BlogMembersMapping blogMembersMapping);
    void deleteByBlogMemberMappingId(Long blogMembersId);
    Optional<BlogMembersMapping> findByBlogMembersId(Long blogMemberId);
    Optional<BlogMembersMapping> findByMbNoAndBlogId(Long mbNo, Long blogId);
}