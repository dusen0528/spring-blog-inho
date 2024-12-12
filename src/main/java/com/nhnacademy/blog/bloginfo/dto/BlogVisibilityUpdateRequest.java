package com.nhnacademy.blog.bloginfo.dto;

public class BlogVisibilityUpdateRequest {
    private final Long blogId;
    private final Boolean blogIsPublic;

    public BlogVisibilityUpdateRequest(Long blogId, Boolean blogIsPublic) {
        this.blogId = blogId;
        this.blogIsPublic = blogIsPublic;
    }

    public Long getBlogId() {
        return blogId;
    }

    public Boolean getBlogIsPublic() {
        return blogIsPublic;
    }
}
