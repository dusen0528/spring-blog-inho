package com.nhnacademy.blog.tag.domain;

public class Tag {
    private final Long tagId;
    private final String tagName;

    private Tag(Long tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public static Tag ofNewTag(String tagName) {
        return new Tag(null, tagName);
    }

    public static Tag ofExistingTag(Long tagId, String tagName) {
        return new Tag(tagId, tagName);
    }

    public Long getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
