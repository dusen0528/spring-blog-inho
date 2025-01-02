package com.nhnacademy.blog.bloginfo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
@SuppressWarnings("java:S107")

/**
 * TODO#2 - blog Entity 구현
 */

@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long blogId;

    @Column(nullable = false, unique = true, length = 50)
    private String blogFid;

    @Column(nullable = false,columnDefinition = "tinyint")
    private boolean blogMain;

    @Column(nullable = false, length = 100)
    private String blogName;

    @Column(nullable = false, length = 100)
    private String blogMbNickname;

    /**
     * columnDefinition 속성을 사용하여 데이터베이스 컬럼의 타입을 직접 지정할 수 있습니다.
     */
    @Column(columnDefinition = "text")
    private String blogDescription;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "tinyint")
    private Boolean blogIsPublic = true;

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

    public Blog() {

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

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String blogName, String blogMbNickname, String blogDescription, Boolean blogIsPublic){
        this.blogName = blogName;
        this.blogMbNickname = blogMbNickname;
        this.blogDescription = blogDescription;
        this.blogIsPublic = blogIsPublic;
    }

    /**
     * 블로그 공개여부 설정
     * @param blogIsPublic
     */
    public void enableBlogPublicAccess(boolean blogIsPublic){
        this.blogIsPublic = blogIsPublic;
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