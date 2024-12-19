package com.nhnacademy.blog.post.dto;

public class PostSearchRequest {
    private final Long blogId;
    private final String keyword;
    private final String categoryId;

    public PostSearchRequest(Long blogId, String keyword, String categoryId) {
        this.blogId = blogId;
        this.keyword = keyword;
        this.categoryId = categoryId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getCategoryId() {
        return categoryId;
    }
}
