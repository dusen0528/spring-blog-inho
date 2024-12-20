package com.nhnacademy.blog.postcategory.dto;

public class PostCategoryCreateRequest {
    private final Long postId;
    private final Long categoryId;

    public PostCategoryCreateRequest(Long postId, Long categoryId) {
        this.postId = postId;
        this.categoryId = categoryId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

}
