package com.nhnacademy.blog.bloginfo.repository;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO#4 - BlogRepository 구현
 */
public interface BlogRepository extends JpaRepository<Blog, Long> {
}
