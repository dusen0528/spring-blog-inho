package com.nhnacademy.blog.bloginfo.repository;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequestDto;

import java.util.Optional;

public interface BlogRepository {
    void save(Blog blog);
    void update(BlogUpdateRequestDto blogUpdateRequestDto);
    void deleteByBlogId(long blogId);
    Optional<Blog> findByBlogId(long blogId);
    boolean existByBlogId(long blogId);
}