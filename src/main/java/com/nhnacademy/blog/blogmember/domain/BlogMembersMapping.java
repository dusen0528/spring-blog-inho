package com.nhnacademy.blog.blogmember.domain;

public class BlogMembersMapping {

    private final Long blogMembersId;
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
    private BlogMembersMapping(Long blogMembersId, Long mbNo, Long blogId, String roleId) {
        this.blogMembersId = blogMembersId;
        this.mbNo = mbNo;
        this.blogId = blogId;
        this.roleId = roleId;
    }

    public static BlogMembersMapping ofNewBlogMemberMapping(Long mbNo, Long blogId, String roleId) {
        return new BlogMembersMapping(null, mbNo, blogId, roleId);
    }

    public static BlogMembersMapping ofExistingBlogMemberMapping(Long blogMembersId,Long mbNo, Long blogId, String roleId) {
        return new BlogMembersMapping(blogMembersId, mbNo, blogId, roleId);
    }

    public Long getBlogMembersId() {
        return blogMembersId;
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
