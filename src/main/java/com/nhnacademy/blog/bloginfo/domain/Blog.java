package com.nhnacademy.blog.bloginfo.domain;

import java.time.LocalDateTime;
@SuppressWarnings("java:S107")
public class Blog {

    private final Long blogId;
    private final String blogFid;
    private final boolean blogMain;
    private final String blogName;
    private final String blogMbNickname;
    private final String blogDescription;
    private final Boolean blogIsPublic;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Blog(Long blogId, String blogFid, boolean blogMain, String blogName, String blogMbNickname, String blogDescription, Boolean blogIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public static Blog ofNewBlog(String blogFid, Boolean blogMain, String blogName, String blogMbNickname, String blogDescription){
        return new Blog(
            null,
                blogFid,
                blogMain,
                blogName,
                blogMbNickname,
                blogDescription,
                true,
                LocalDateTime.now(), null);
    }

    public static Blog ofExistingBlogInfo(Long blogId, String blogFid, Boolean blogMain, String blogName, String blogMbNickname, String blogDescription, Boolean blogIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt){
        return new Blog(
                blogId,
                blogFid,
                blogMain,
                blogName,
                blogMbNickname,
                blogDescription,
                blogIsPublic,
                createdAt, updatedAt);
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

    @Override
    public String toString() {
        return "Blog{" +
                "blogId=" + blogId +
                ", blogFid='" + blogFid + '\'' +
                ", blogMain=" + blogMain +
                ", blogName='" + blogName + '\'' +
                ", blogMbNickname='" + blogMbNickname + '\'' +
                ", blogDescription='" + blogDescription + '\'' +
                ", blogIsPublic=" + blogIsPublic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
