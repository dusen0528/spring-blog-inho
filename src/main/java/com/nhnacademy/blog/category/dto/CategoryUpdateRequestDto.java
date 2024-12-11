package com.nhnacademy.blog.category.dto;

public class CategoryUpdateRequestDto {
    
    private final Long categoryId;
    private final Long categoryPid;
    private final Integer topicId;
    private final String categoryName;
    private final int categorySec;

    public CategoryUpdateRequestDto(Long categoryId, Long categoryPid, Integer topicId, String categoryName, int categorySec) {
        this.categoryId = categoryId;
        this.categoryPid = categoryPid;
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
