package com.nhnacademy.blog.tagging.domain;

public class PostTag {
    private final Long postTagId;
    private final Long tagId;
    private final Long postId;

    private PostTag(Long postTagId, Long tagId, Long postId) {
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

    public static PostTag ofNewPostTag(Long tagId, Long postId) {
        return new PostTag(null, tagId, postId);
    }

    public static PostTag ofExistingPostTag(Long postTagId, Long tagId, Long postId) {
        return new PostTag(postTagId, tagId, postId);
    }

}
