package com.nhnacademy.blog.category.dto;

public class CategoryDeleteRequest {
    private final long categoryId;
    private final long blogId;

    public CategoryDeleteRequest(long categoryId, long blogId) {
        this.categoryId = categoryId;
        this.blogId = blogId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public long getBlogId() {
        return blogId;
    }
}
