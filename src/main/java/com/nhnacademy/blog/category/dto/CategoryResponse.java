package com.nhnacademy.blog.category.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {

    private final Long categoryId;
    private final Long categoryPid;
    private final Long blogId;
    private final Integer topicId;
    private final String categoryName;
    private final Integer categorySec;

    List<CategoryResponse> subCategories = new ArrayList<>();

    public CategoryResponse(Long categoryId, Long categoryPid, Long blogId, Integer topicId, String categoryName, Integer categorySec) {
        this.categoryId = categoryId;
        this.categoryPid = categoryPid;
        this.blogId = blogId;
        this.topicId = topicId;
        this.categoryName = categoryName;
        this.categorySec = categorySec;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getCategoryPid() {
        return categoryPid;
    }

    public Long getBlogId() {
        return blogId;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Integer getCategorySec() {
        return categorySec;
    }

    public List<CategoryResponse> getSubCategories() {
        return this.subCategories;
    }

    public void addSubCategory(CategoryResponse categoryResponse) {
        this.subCategories.add(categoryResponse);
    }

    @Override
    public String toString() {
        return "CategoryResponse{" +
                "categoryId=" + categoryId +
                ", categoryPid=" + categoryPid +
                ", blogId=" + blogId +
                ", topicId=" + topicId +
                ", categoryName='" + categoryName + '\'' +
                ", categorySec=" + categorySec +
                ", subCategories=" + subCategories +
                '}';
    }
}
