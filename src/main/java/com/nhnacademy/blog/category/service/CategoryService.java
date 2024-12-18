package com.nhnacademy.blog.category.service;

import com.nhnacademy.blog.category.dto.*;

import java.util.List;

public interface CategoryService {
    //root category 생성
    CategoryResponse createRootCategory(RootCategoryCreateRequest rootCategoryCreateRequest);
    CategoryResponse initializeCreateRootCategory(RootCategoryCreateRequest rootCategoryCreateRequest);
    //sub category 생성
    CategoryResponse createSubCategory(SubCategoryCreateRequest subCategoryCreateRequest);
    //카테고리 수정
    CategoryResponse updateRootCategory(RootCategoryUpdateRequest rootCategoryUpdateRequest);
    CategoryResponse updateSubCategory(SubCategoryUpdateRequest subCategoryUpdateRequest);
    //카테고리 삭제
    void deleteCategory(CategoryDeleteRequest categoryDeleteRequest);
    //해당 블로그의 모든 카테고리 조회
    List<CategoryResponse> getAllCategories(long blogId);

}
