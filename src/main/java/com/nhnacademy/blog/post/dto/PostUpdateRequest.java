package com.nhnacademy.blog.post.dto;

public class PostUpdateRequest {
    private final Long postId;
    private final String postTitle;
    private final String postContent;
    private final boolean postIsPublic;

    public PostUpdateRequest(Long postId, String postTitle, String postContent, boolean postIsPublic) {
        this.postId = postId;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postIsPublic = postIsPublic;
    }

    public Long getPostId() {
        return postId;
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
