package com.nhnacademy.blog.category.domain;


import java.time.LocalDateTime;
import java.util.Objects;
@SuppressWarnings("java:S107")
public class Category {

    private final Long categoryId;
    private final Long categoryPid;
    private final Long blogId;
    private final Integer topicId;
    private final String categoryName;
    private final Integer categorySec;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Category(Long categoryId, Long categoryPid, Long blogId, Integer topicId, String categoryName, Integer categorySec, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static Category ofNewSubCategory(Long categoryPid,Long blogId,Integer topicId, String categoryName, Integer categorySec){
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

    public static Category ofExistingCategory(Long categoryId, Long categoryPid, Long blogId, Integer topicId, String categoryName, Integer categorySec, LocalDateTime createdAt, LocalDateTime updatedAt){
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(categoryId, category.categoryId) && Objects.equals(categoryPid, category.categoryPid) && Objects.equals(blogId, category.blogId) && Objects.equals(topicId, category.topicId) && Objects.equals(categoryName, category.categoryName) && Objects.equals(categorySec, category.categorySec) && Objects.equals(createdAt, category.createdAt) && Objects.equals(updatedAt, category.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, categoryPid, blogId, topicId, categoryName, categorySec, createdAt, updatedAt);
    }
}
