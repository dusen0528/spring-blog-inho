package com.nhnacademy.blog.category.dto;

public class CategoryUpdateRequestDto {
    
    private final Integer categoryId;
    private final Integer categoryPid;
    private final Integer topicId;
    private final String categoryName;
    private final int categorySec;

    public CategoryUpdateRequestDto(Integer categoryId, Integer categoryPid, Integer topicId, String categoryName, int categorySec) {
        this.categoryId = categoryId;
        this.categoryPid = categoryPid;
        this.topicId = topicId;
        this.categoryName = categoryName;
        this.categorySec = categorySec;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Integer getCategoryPid() {
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
