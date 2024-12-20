package com.nhnacademy.blog.postcategory.domain;

public class PostCategory {

    private final Long postCategoryId;
    private final Long postId;
    private final Long categoryId;

    private PostCategory(Long postCategoryId, Long postId, Long categoryId) {
        this.postCategoryId = postCategoryId;
        this.postId = postId;
        this.categoryId = categoryId;
    }

    public static PostCategory ofNewPostCategory(Long postId, Long categoryId) {
        return new PostCategory(null, postId, categoryId);
    }

    public static PostCategory ofExistingPostCategory(Long postCategoryId, Long postId, Long categoryId) {
        return new PostCategory(postCategoryId, postId, categoryId);
    }

    public Long getPostCategoryId() {
        return postCategoryId;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

}
