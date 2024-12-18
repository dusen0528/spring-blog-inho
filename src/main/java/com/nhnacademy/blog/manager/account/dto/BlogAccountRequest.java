package com.nhnacademy.blog.manager.account.dto;

public class BlogAccountRequest {

    //이메일
    private final String email;

    //회원_이름
    private final String name;

    //회원_비밀번호
    private final String password;

    //모바일 연락처
    private final String mobile;


    //블로그_식별_아이디
    private final String blogFid;

    public BlogAccountRequest(String email, String name, String password, String mobile, String blogFid) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.mobile = mobile;
        this.blogFid = blogFid;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getMobile() {
        return mobile;
    }

    public String getBlogFid() {
        return blogFid;
    }
}