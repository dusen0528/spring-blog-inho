package com.nhnacademy.blog.bloginfo.domain;

import java.time.LocalDateTime;

public class Blog {

    private final Long blogId;
    private final String blogName;
    private final String blogMbNickname;
    private final String blogDescription;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Blog(Long blogId, String blogName, String blogMbNickname, String blogDescription, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.blogId = blogId;
        this.blogName = blogName;
        this.blogMbNickname = blogMbNickname;
        this.blogDescription = blogDescription;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Blog ofNewBlog(String blogName, String blogMbNickname, String blogDescription){
        return new Blog(
            null,
                blogName,
                blogMbNickname,
                blogDescription,
                LocalDateTime.now(),
                null
        );
    }

    public static Blog ofExistingBlogInfo(Long blogId, String blogName, String blogMbNickname, String blogDescription, LocalDateTime createdAt, LocalDateTime updatedAt){
        return new Blog(
                blogId,
                blogName,
                blogMbNickname,
                blogDescription,
                createdAt,
                updatedAt
        );
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
