package com.nhnacademy.blog.manager.account.impl;

import com.nhnacademy.blog.bloginfo.dto.BlogCreateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.service.BlogInfoService;
import com.nhnacademy.blog.bloginfo.service.impl.BlogInfoServiceImpl;
import com.nhnacademy.blog.blogmember.dto.BlogMemberRegisterRequest;
import com.nhnacademy.blog.blogmember.service.BlogMemberService;
import com.nhnacademy.blog.blogmember.service.impl.BlogMemberServiceImpl;
import com.nhnacademy.blog.category.dto.RootCategoryCreateRequest;
import com.nhnacademy.blog.category.service.CategoryService;
import com.nhnacademy.blog.category.service.impl.CategoryServiceImpl;
import com.nhnacademy.blog.common.annotation.InitOrder;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.manager.account.AbstractBlogAccountManager;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.service.MemberService;
import com.nhnacademy.blog.member.service.impl.MemberServiceImpl;

@InitOrder(Integer.MAX_VALUE)
@Service(DefaultBlogAccountManager.BEAN_NAME)
public class DefaultBlogAccountManager extends AbstractBlogAccountManager {

    public static final String BEAN_NAME = "defaultBlogAccountManager";
    private final MemberService memberService;
    private final BlogMemberService blogMemberService;
    private final BlogInfoService blogInfoService;
    private final CategoryService categoryService;

    public DefaultBlogAccountManager(
            @Qualifier(MemberServiceImpl.BEAN_NAME) MemberService memberService,
            @Qualifier(BlogMemberServiceImpl.BEAN_NAME) BlogMemberService blogMemberService,
            @Qualifier(BlogInfoServiceImpl.BEAN_NAME) BlogInfoService blogInfoService,
            @Qualifier(CategoryServiceImpl.BEAN_NAME) CategoryService categoryService)
    {
        this.memberService = memberService;
        this.blogMemberService = blogMemberService;
        this.blogInfoService = blogInfoService;
        this.categoryService = categoryService;
    }

    @Override
    protected MemberResponse registerMember(String email, String name, String password, String mobile) {
        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
                email,
                name,
                password,
                mobile
        );
        return memberService.registerMember(memberRegisterRequest);
    }

    @Override
    protected BlogResponse createBlog(long mbNo, String blogFid, String blogName, String blogMbNickName, String blogDescription) {
        BlogCreateRequest blogCreateRequest = new BlogCreateRequest(
                mbNo,
                blogFid,
                blogName,
                blogMbNickName,
                blogDescription
        );
        return blogInfoService.createBlog(blogCreateRequest);
    }

    @Override
    protected void registerBlogMemberMapping(long mbNo, long blogId) {
        //사용자 권한으로 등록
        BlogMemberRegisterRequest blogMemberRegisterRequest = new BlogMemberRegisterRequest(mbNo,blogId,"ROLE_OWNER");
        blogMemberService.registerBlogMember(blogMemberRegisterRequest);
    }

    @Override
    protected void registerDefaultCategory(long blogId) {
        RootCategoryCreateRequest rootCategoryCreateRequest = new RootCategoryCreateRequest(
                blogId,
                null,
                "nhnacademy",
                1
        );
        categoryService.initializeCreateRootCategory(rootCategoryCreateRequest);
    }
}
