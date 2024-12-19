package com.nhnacademy.blog.post.dto;

public class PostSearchRequest {

    private final Long blogId;
    private final Long categoryId;
    private final boolean postIsPublic;

    public PostSearchRequest(Long blogId, Long categoryId, boolean postIsPublic) {
        this.blogId = blogId;
        this.categoryId = categoryId;
        this.postIsPublic = postIsPublic;
    }

    public Long getBlogId() {
        return blogId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public boolean isPostIsPublic() {
        return postIsPublic;
    }

}
