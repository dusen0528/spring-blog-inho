package com.nhnacademy.blog.bloginfo.dto;

public class BlogCreateRequest {
    //회원_번호
    private final Long mbNo;

    //블로그_식별_아이디
    private final String blogFid;
    //블로그_이름
    private final String blogName;
    //블로그_사용자 별명
    private final String blogMbNickname;
    //블로그_설명
    private final String blogDescription;

    public BlogCreateRequest(Long mbNo, String blogFid, String blogName, String blogMbNickname, String blogDescription) {
        this.mbNo = mbNo;
        this.blogFid = blogFid;
        this.blogName = blogName;
        this.blogMbNickname = blogMbNickname;
        this.blogDescription = blogDescription;
    }

    public Long getMbNo() {
        return mbNo;
    }

    public String getBlogFid() {
        return blogFid;
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
