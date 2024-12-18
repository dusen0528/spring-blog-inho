package com.nhnacademy.blog.manager.account;

import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.manager.account.dto.BlogAccountRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;

public abstract class AbstractBlogAccountManager {

    /**
     * 블로그 계정 생성 ( 회원가입, 블로그 생성, 기본카테고리 생성해주는 기본 Template)
     * @param blogAccountRequest
     * @return
     */
    public final BlogResponse createAccount(BlogAccountRequest blogAccountRequest) {

        MemberResponse memberResponse = registerMember(blogAccountRequest.getEmail(), blogAccountRequest.getName(), blogAccountRequest.getPassword(), blogAccountRequest.getMobile());
        long memberNo = memberResponse.getMbNo();

        BlogResponse blogResponse = createBlog(memberNo, blogAccountRequest.getBlogFid(), generateDefaultBlogName(blogAccountRequest.getBlogFid()), generateDefaultBlogMbNickName(), generateDefaultBlogDescription());
        long blogId = blogResponse.getBlogId();

        registerBlogMemberMapping(memberNo,blogId);
        registerDefaultCategory(blogId);

        return blogResponse;
    }

    //1. 블로그 사용자 등록(members)
    protected abstract MemberResponse registerMember(String email, String name, String password, String mobile);

    //2. 블로그 생성(blogs)
    protected abstract BlogResponse createBlog(long mbNo, String blogFid, String blogName, String blogMbNickName, String blogDescription);

    //3. 블로그 권한 설정
    protected abstract void registerBlogMemberMapping(long mbNo, long blogId);

    //4. 블로그 카테고리 기본카테고리 생성및 topic 연결
    protected abstract void registerDefaultCategory(long blogId);

    //기본 blog이름생성
    protected String generateDefaultBlogName(String s){
        return "%s 's blog".formatted(s);
    }

    //블로그에서 노출될 사용자의 별명
    protected String generateDefaultBlogMbNickName(){
        return "changeMe";
    }

    //블로그 설명
    protected String generateDefaultBlogDescription(){
        return "welcome to my blog!";
    }

}
