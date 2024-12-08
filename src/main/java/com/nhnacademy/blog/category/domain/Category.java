package com.nhnacademy.blog.category.domain;

import java.time.LocalDateTime;

public class Category {

    private final Integer categoryId;
    private final Integer categoryPid;
    private final long blogId;
    private final String categoryName;
    private final int categorySec;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Category(Integer categoryId, Integer categoryPid, long blogId, String categoryName, int categorySec, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.categoryId = categoryId;
        this.categoryPid = categoryPid;
        this.blogId = blogId;
        this.categoryName = categoryName;
        this.categorySec = categorySec;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Category ofNewRootCategory(long blogId, String categoryName, int categorySec){
        return new Category(
                null,
                null,
                blogId,
                categoryName,
                categorySec,
                LocalDateTime.now(),
                null
        );
    }

    public static Category ofNewSubCategory(Integer categoryPid,long blogId, String categoryName, int categorySec){
        return new Category(
                null,
                categoryPid,
                blogId,
                categoryName,
                categorySec,
                LocalDateTime.now(),
                null
        );
    }

    public static Category ofExistingCategory(int categoryId, int categoryPid, long blogId, String categoryName, int categorySec, LocalDateTime createdAt, LocalDateTime updatedAt){
        return new Category(
                categoryId,
                categoryPid,
                blogId,
                categoryName,
                categorySec,
                createdAt,
                updatedAt
        );
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getCategoryPid() {
        return categoryPid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public int getCategorySec() {
        return categorySec;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
