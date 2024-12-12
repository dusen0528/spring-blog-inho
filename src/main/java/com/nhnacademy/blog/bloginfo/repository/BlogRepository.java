package com.nhnacademy.blog.bloginfo.repository;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;

import java.util.Optional;

public interface BlogRepository {
    void save(Blog blog);
    void update(BlogUpdateRequest blogUpdateRequest);
    void deleteByBlogId(long blogId);
    Optional<Blog> findByBlogId(long blogId);
    boolean existByBlogId(long blogId);
    boolean existByBlogFid(String blogFid);
    boolean existMainBlogByMbNo(long mbNo);
}