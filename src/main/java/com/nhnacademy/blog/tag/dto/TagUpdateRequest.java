package com.nhnacademy.blog.tag.dto;

public class TagUpdateRequest {
    private final Long tagId;
    private final String tagName;

    public TagUpdateRequest(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public Long getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }
}
