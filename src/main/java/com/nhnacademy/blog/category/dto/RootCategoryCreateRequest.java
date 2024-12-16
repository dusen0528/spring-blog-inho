package com.nhnacademy.blog.category.dto;

public class RootCategoryCreateRequest {

    private final Long blogId;
    private final Integer topicId;
    private final String categoryName;
    private final Integer categorySec;

    public RootCategoryCreateRequest(Long blogId, Integer topicId, String categoryName, Integer categorySec) {
        this.blogId = blogId;
        this.topicId = topicId;
        this.categoryName = categoryName;
        this.categorySec = categorySec;
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

}
