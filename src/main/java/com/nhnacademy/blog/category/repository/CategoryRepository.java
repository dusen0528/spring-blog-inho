package com.nhnacademy.blog.category.repository;

import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryResponse;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequest;


import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    //카테고리 저장
    void save(Category category);
    //카테고리 수정
    void update(CategoryUpdateRequest categoryUpdateRequest);

    //카테고라 삭제
    void deleteByCategoryId(Long categoryId);
    //카테고리 조회
    Optional<Category> findByCategoryId(Long categoryId);
    //카테고리 리스트
    List<Category> findAll(Long blogId,Long categoryPid);
    //해당 블로그의 모든 category리스트
    List<CategoryResponse> findAllByBlogId(Long blogId);

    //카테고리 존재여부 체크
    boolean existsByCategoryId(Long categoryId);

    //서브카테고리 존재여부 체크
    boolean existsSubCategoryByCategoryId(Long categoryId);
}
