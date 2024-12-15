package com.nhnacademy.blog.blogmember.dto;

public class BlogMemberRegisterRequest {
    private final Long mbNo;
    private final Long blogId;
    private final String roleId;

    public BlogMemberRegisterRequest(Long mbNo, Long blogId, String roleId) {
        this.mbNo = mbNo;
        this.blogId = blogId;
        this.roleId = roleId;
    }

    public Long getMbNo() {
        return mbNo;
    }

    public Long getBlogId() {
        return blogId;
    }

    public String getRoleId() {
        return roleId;
    }

}
