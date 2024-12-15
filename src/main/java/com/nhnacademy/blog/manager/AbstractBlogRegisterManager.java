package com.nhnacademy.blog.manager;

public abstract class AbstractBlogRegisterManager {

    public final void register() {
        registerMember();
        registerBlog();
        registerDefaultCategory();
        registerBlogMemberMapping();
    }

    //1. 블로그 사용자 등록(members)
    abstract void registerMember();

    //2. 블로그 생성(blogs)
    abstract void registerBlog();

    //3. 블로그 카테고리 기본카테고리 생성및 topic연결
    abstract void registerDefaultCategory();

    //4. 블로그 권한 설정
    abstract void registerBlogMemberMapping();

}
