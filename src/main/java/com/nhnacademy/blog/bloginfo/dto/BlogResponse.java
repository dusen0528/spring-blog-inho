package com.nhnacademy.blog.bloginfo.dto;

import java.time.LocalDateTime;

public class BlogResponse {

    private final Long blogId;
    private final String blogFid;
    private final boolean blogMain;
    private final String blogName;
    private final String blogMbNickname;
    private final String blogDescription;
    private final Boolean blogIsPublic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public BlogResponse(Long blogId, String blogFid, boolean blogMain, String blogName, String blogMbNickname, String blogDescription, Boolean blogIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.blogId = blogId;
        this.blogFid = blogFid;
        this.blogMain = blogMain;
        this.blogName = blogName;
        this.blogMbNickname = blogMbNickname;
        this.blogDescription = blogDescription;
        this.blogIsPublic = blogIsPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getBlogId() {
        return blogId;
    }

    public String getBlogFid() {
        return blogFid;
    }

    public boolean isBlogMain() {
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

    public Boolean getBlogIsPublic() {
        return blogIsPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}
