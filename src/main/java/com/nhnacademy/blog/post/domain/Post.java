package com.nhnacademy.blog.post.domain;

import java.time.LocalDateTime;

public class Post {

    private final Long postId;
    private final Long blogId;
    private final Long createdMbNo;
    private final Long updatedMbNo;
    private final String postTitle;
    private final String postContent;
    private final boolean postIsPublic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Post(Long postId, Long blogId, Long createdMbNo, Long updatedMbNo, String postTitle, String postContent, boolean postIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.postId = postId;
        this.blogId = blogId;
        this.createdMbNo = createdMbNo;
        this.updatedMbNo = updatedMbNo;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postIsPublic = postIsPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Post ofNewPost(Long blogId, Long createdMbNo, String postTitle, String postContent, boolean postIsPublic){
        return new Post(null,blogId,createdMbNo,null,postTitle,postContent,postIsPublic,LocalDateTime.now(),null);
    }

    public static Post ofExistingPost(Long postId, Long blogId, Long createdMbNo, Long updatedMbNo, String postTitle, String postContent, boolean postIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt){
        return new Post(postId,blogId,createdMbNo,updatedMbNo,postTitle,postContent,postIsPublic,createdAt,updatedAt);
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

    public Long getUpdatedMbNo() {
        return updatedMbNo;
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
