package com.nhnacademy.blog.category.dto;

public class RootCategoryUpdateRequest {

    private final Long categoryId;
    private final Long blogId;
    private final Integer topicId;
    private final String categoryName;
    private final int categorySec;

    public RootCategoryUpdateRequest(Long categoryId, Long blogId, Integer topicId, String categoryName, int categorySec) {
        this.categoryId = categoryId;
        this.blogId = blogId;
        this.topicId = topicId;
        this.categoryName = categoryName;
        this.categorySec = categorySec;
    }

    public Long getCategoryId() {
        return categoryId;
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

    public int getCategorySec() {
        return categorySec;
    }

}
