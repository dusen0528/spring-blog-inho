package com.nhnacademy.blog.category.dto;

public class CategoryDeleteRequest {
    private final Long categoryId;
    private final Long blogId;

    public CategoryDeleteRequest(Long categoryId, Long blogId) {
        this.categoryId = categoryId;
        this.blogId = blogId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getBlogId() {
        return blogId;
    }
}
