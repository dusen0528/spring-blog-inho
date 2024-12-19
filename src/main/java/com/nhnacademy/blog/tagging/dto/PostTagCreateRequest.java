package com.nhnacademy.blog.tagging.dto;

public class PostTagCreateRequest {

    private final Long tagId;
    private final Long postId;

    public PostTagCreateRequest(Long tagId, Long postId) {
        this.tagId = tagId;
        this.postId = postId;
    }

    public Long getTagId() {
        return tagId;
    }

    public Long getPostId() {
        return postId;
    }
}
