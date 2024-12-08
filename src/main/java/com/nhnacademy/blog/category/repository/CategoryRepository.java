package com.nhnacademy.blog.category.repository;

import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequestDto;
import com.nhnacademy.blog.common.websupport.PageRequest;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    //카테고리 저장
    int save(Category category);
    //카테고리 수정
    int update(CategoryUpdateRequestDto categoryUpdateRequestDto);
    //카테고라 삭제
    int delete(int categoryId);
    //카테고리 조회
    Optional<Category> findByCategoryId(int categoryId);
    //카테고리 리스트
    List<Category> findAllByPageRequest(PageRequest pageRequest, Integer categoryPid);
    //카테고리 리스트의 전체 row수
    long count(Integer categoryPid);
    //카타게로 존재여부 체크
    boolean existsByCategoryId(int categoryId);
}
