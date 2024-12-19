package com.nhnacademy.blog.post.dto;

import java.time.LocalDateTime;

public class PostResponse {

    private final Long postId;
    private final Long blogId;

    private final Long createdMbNo;
    private final String createdMbName;

    private final Long updatedMbNo;
    private final String updatedMbName;

    private final String postTitle;
    private final String postContent;
    private final boolean postIsPublic;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Long postId, Long blogId, Long createdMbNo, String createdMbName, Long updatedMbNo, String updatedMbName, String postTitle, String postContent, boolean postIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.blogId = blogId;
        this.createdMbNo = createdMbNo;
        this.createdMbName = createdMbName;
        this.updatedMbNo = updatedMbNo;
        this.updatedMbName = updatedMbName;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postIsPublic = postIsPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public Long getCreatedMbNo() {
        return createdMbNo;
    }

    public String getCreatedMbName() {
        return createdMbName;
    }

    public Long getUpdatedMbNo() {
        return updatedMbNo;
    }

    public String getUpdatedMbName() {
        return updatedMbName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
