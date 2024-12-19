package com.nhnacademy.blog.post.dto;

public class PostCreateRequest {
    private final Long blogId;
    private final String postTitle;
    private final String postContent;
    private final boolean postIsPublic;

    public PostCreateRequest(Long blogId, String postTitle, String postContent, boolean postIsPublic) {
        this.blogId = blogId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postIsPublic = postIsPublic;
    }

    public Long getBlogId() {
        return blogId;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public boolean isPostIsPublic() {
        return postIsPublic;
    }
}