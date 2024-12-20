package com.nhnacademy.blog.postcategory.repository;

import com.nhnacademy.blog.postcategory.domain.PostCategory;
import java.util.Optional;

public interface PostCategoryRepository {

    void save(PostCategory postCategory);
    void deleteByPostCategoryId(Long postCategoryId);
    void deleteByPostIdAndCategoryId(Long postId, Long categoryId);

    Optional<PostCategory> findByPostCategoryId(Long postCategoryId);
    Optional<PostCategory> findByPostIdAndCategoryId(Long postId, Long categoryId);

    boolean existsByPostCategoryId(Long postCategoryId);
    boolean existsByPostIdAndCategoryId(Long postId, Long categoryId);

}