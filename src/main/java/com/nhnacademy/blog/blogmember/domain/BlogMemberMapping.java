package com.nhnacademy.blog.blogmember.domain;

public class BlogMemberMapping {

    private final Long blogMemberId;
    private final Long mbNo;
    private final Long blogId;
    private final String roleId;

    enum Role{

        ROLE_ADMIN("시스템_관리자"),
        ROLE_USER("블로그_회원"),
        ROLE_OWNER("블로그_소유자");

        private final String roleName;
        Role(String roleName) {
            this.roleName = roleName;
        }
        public String getRoleName() {
            return roleName;
        }
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

    public static BlogMemberMapping ofExistingBlogMemberMapping(Long blogMemberId,Long mbNo, Long blogId, String roleId) {
        return new BlogMemberMapping(blogMemberId, mbNo, blogId, roleId);
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
