package com.nhnacademy.blog.tagging.domain;

public class PostTag {
    private final Long postTagId;
    private final Long postId;
    private final Long tagId;


    private PostTag(Long postTagId, Long postId, Long tagId) {
        this.postTagId = postTagId;
        this.postId = postId;
        this.tagId = tagId;
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

    public static PostTag ofNewPostTag(Long postId,Long tagId) {
        return new PostTag(null, postId, tagId );
    }

    public static PostTag ofExistingPostTag(Long postTagId, Long postId, Long tagId) {
        return new PostTag(postTagId, postId, tagId );
    }

}
