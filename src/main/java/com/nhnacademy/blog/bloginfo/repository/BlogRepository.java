package com.nhnacademy.blog.bloginfo.repository;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequestDto;

import java.util.Optional;

public interface BlogRepository {
    int save(Blog blog);
    int update(BlogUpdateRequestDto blogUpdateRequestDto);
    int delete(long blogId);
    Optional<Blog> findByBlogId(long blogId);
    boolean existByBlogId(long blogId);
}