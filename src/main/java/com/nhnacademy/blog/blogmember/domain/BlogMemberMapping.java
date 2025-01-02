package com.nhnacademy.blog.blogmember.domain;

import jakarta.persistence.*;

/**
 * TODO#5 BlogMemberMapping entity 구현
 */

@Entity
@Table(name = "blog_member_mapping")
public class BlogMemberMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogMemberId;
    private Long mbNo;
    private Long blogId;
    private String roleId;

    public BlogMemberMapping() {

    }

    private BlogMemberMapping(Long blogMemberId, Long mbNo, Long blogId, String roleId) {
        this.blogMemberId = blogMemberId;
        this.mbNo = mbNo;
        this.blogId = blogId;
        this.roleId = roleId;
    }

    public static BlogMemberMapping ofNewBlogMemberMapping(Long mbNo, Long blogId, String roleId) {
        return new BlogMemberMapping(null, mbNo, blogId, roleId);
    }

    public Long getBlogMemberId() {
        return blogMemberId;
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
