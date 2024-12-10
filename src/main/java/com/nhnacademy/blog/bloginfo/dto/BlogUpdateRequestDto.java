package com.nhnacademy.blog.bloginfo.dto;

public class BlogUpdateRequestDto {

    private final Long blogId;
    private final Boolean blogMain;
    private final String blogName;
    private final String blogMbNickname;
    private final String blogDescription;

    public BlogUpdateRequestDto(Long blogId, Boolean blogMain, String blogName, String blogMbNickname, String blogDescription) {
        this.blogId = blogId;
        this.blogMain = blogMain;
        this.blogName = blogName;
        this.blogMbNickname = blogMbNickname;
        this.blogDescription = blogDescription;
    }

    public Long getBlogId() {
        return blogId;
    }

    public Boolean isBlogMain() {
        return blogMain;
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
