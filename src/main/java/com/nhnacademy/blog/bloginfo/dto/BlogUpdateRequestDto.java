package com.nhnacademy.blog.bloginfo.dto;

public class BlogUpdateRequestDto {

    private final Long blogId;
    private final String blogName;
    private final String blogMbNickname;
    private final String blogDescription;

    public BlogUpdateRequestDto(Long blogId, String blogName, String blogMbNickname, String blogDescription) {
        this.blogId = blogId;
        this.blogName = blogName;
        this.blogMbNickname = blogMbNickname;
        this.blogDescription = blogDescription;
    }

    public Long getBlogId() {
        return blogId;
    }

    public String getBlogName() {
        return blogName;
    }

    public String getBlogMbNickname() {
        return blogMbNickname;
    }

    public String getBlogDescription() {
        return blogDescription;
    }

}
