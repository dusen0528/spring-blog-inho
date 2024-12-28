package com.nhnacademy.blog.category.domain;


import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("java:S107")
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private Long categoryPid;
    private Long blogId;
    private Integer topicId;
    private String categoryName;
    private Integer categorySec;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    public Category() {

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

    public void update(Long categoryPid, Integer topicId,String categoryName,Integer categorySec){
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


    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryPid=" + categoryPid +
                ", blogId=" + blogId +
                ", topicId=" + topicId +
                ", categoryName='" + categoryName + '\'' +
                ", categorySec=" + categorySec +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
