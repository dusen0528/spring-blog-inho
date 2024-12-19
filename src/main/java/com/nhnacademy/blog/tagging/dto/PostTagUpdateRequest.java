package com.nhnacademy.blog.tagging.dto;

public class PostTagUpdateRequest {

    private final Long postTagId;
    private final Long tagId;
    private final Long postId;

    public PostTagUpdateRequest(Long postTagId, Long tagId, Long postId) {
        this.postTagId = postTagId;
        this.tagId = tagId;
        this.postId = postId;
    }

    public Long getPostTagId() {
        return postTagId;
    }

    public Long getTagId() {
        return tagId;
    }

    public Long getPostId() {
        return postId;
    }
}
