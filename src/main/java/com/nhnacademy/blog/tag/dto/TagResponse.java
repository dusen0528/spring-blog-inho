package com.nhnacademy.blog.tag.dto;

public class TagResponse {
    private final Long tagId;
    private final String tagName;

    public TagResponse(Long tagId, String tagName) {
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
