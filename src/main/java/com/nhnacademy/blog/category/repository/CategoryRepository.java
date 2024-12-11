package com.nhnacademy.blog.category.repository;

import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequestDto;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    //카테고리 저장
    void save(Category category);
    //카테고리 수정
    void update(CategoryUpdateRequestDto categoryUpdateRequestDto);
    //카테고라 삭제
    void deleteByCategoryId(Long categoryId);
    //카테고리 조회
    Optional<Category> findByCategoryId(Long categoryId);
    //카테고리 리스트
    List<Category> findAll(Long blogId,Long categoryPid);
    //카타게로 존재여부 체크
    boolean existsByCategoryId(Long categoryId);
}
