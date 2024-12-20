package com.nhnacademy.blog.post.dto;

public class PostSearchParam {

    private final Long blogId;
    private final Long categoryId;
    private final Boolean postIsPublic;

    public PostSearchParam(Long blogId, Long categoryId, Boolean postIsPublic) {
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

    public Boolean isPostIsPublic() {
        return postIsPublic;
    }

    @Override
    public String toString() {
        return "PostSearchRequest{" +
                "blogId=" + blogId +
                ", categoryId=" + categoryId +
                ", postIsPublic=" + postIsPublic +
                '}';
    }
}
