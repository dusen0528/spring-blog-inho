package com.nhnacademy.blog.tag.dto;

public class TagCreateRequest {

    private final String tagName;

    public TagCreateRequest(String tagName) {
        this.tagName = tagName;
    }

    public String getTagName() {
        return tagName;
    }

}
