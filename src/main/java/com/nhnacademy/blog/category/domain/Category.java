package com.nhnacademy.blog.category.domain;

import java.time.LocalDateTime;

public class Category {

    private final Integer categoryId;
    private final Integer categoryPid;
    private final Long blogId;
    private final Integer topicId;
    private final String categoryName;
    private final Integer categorySec;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Category(Integer categoryId, Integer categoryPid, Long blogId, Integer topicId, String categoryName, Integer categorySec, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.categoryId = categoryId;
        this.categoryPid = categoryPid;
        this.blogId = blogId;
        this.topicId = topicId;
        this.categoryName = categoryName;
        this.categorySec = categorySec;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Category ofNewRootCategory(Long blogId,Integer topicId, String categoryName, Integer categorySec){
        return new Category(
                null,
                null,
                blogId,
                topicId,
                categoryName,
                categorySec,
                LocalDateTime.now(),
                null
        );
    }

    public static Category ofNewSubCategory(Integer categoryPid,Long blogId,Integer topicId, String categoryName, Integer categorySec){
        return new Category(
                null,
                categoryPid,
                blogId,
                topicId,
                categoryName,
                categorySec,
                LocalDateTime.now(),
                null
        );
    }

    public static Category ofExistingCategory(Integer categoryId, Integer categoryPid, Long blogId, Integer topicId, String categoryName, Integer categorySec, LocalDateTime createdAt, LocalDateTime updatedAt){
        return new Category(
                categoryId,
                categoryPid,
                blogId,
                topicId,
                categoryName,
                categorySec,
                createdAt,
                updatedAt
        );
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public Integer getCategoryPid() {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
