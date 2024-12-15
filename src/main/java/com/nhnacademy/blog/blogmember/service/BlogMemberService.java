package com.nhnacademy.blog.blogmember.service;

import com.nhnacademy.blog.blogmember.dto.BlogMemberRegisterRequest;

public interface BlogMemberService {
    //blog에 권한을 추가 합니다.
    void registerBlogMember(BlogMemberRegisterRequest blogMemberRegisterRequest);
    //blog에서 권한을 삭제 합니다.
    void removeBlogMember(Long mbNo,Long blogId);

}
