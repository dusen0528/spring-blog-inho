package com.nhnacademy.blog.postcategory.dto;

public class PostCategoryResponse {
    private final Long postCategoryId;
    private final Long postId;
    private final Long categoryId;

    public PostCategoryResponse(Long postCategoryId, Long postId, Long categoryId) {
        this.postCategoryId = postCategoryId;
        this.postId = postId;
        this.categoryId = categoryId;
    }

    public Long getPostCategoryId() {
        return postCategoryId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
