package com.nhnacademy.blog.category.repository;

import com.nhnacademy.blog.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaCategoryRepository extends JpaRepository<Category, Long> {


    List<Category> findAllByBlogId(Long blogId);

    List<Category> findAllByBlogIdAndCategoryPid(Long blogId, Long categoryPid);

    boolean existsByCategoryPid(Long categoryPid);
}
