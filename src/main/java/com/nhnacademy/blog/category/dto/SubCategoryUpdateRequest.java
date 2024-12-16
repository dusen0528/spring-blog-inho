package com.nhnacademy.blog.category.dto;

public class SubCategoryUpdateRequest {
    
    private final Long categoryId;
    private final Long categoryPid;
    private final Long blogId;
    private final Integer topicId;
    private final String categoryName;
    private final Integer categorySec;

    public SubCategoryUpdateRequest(Long categoryId, Long categoryPid, Long blogId, Integer topicId, String categoryName, Integer categorySec) {
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
}
