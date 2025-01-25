package com.nhnacademy.blog.member.service;

import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;

public interface MemberService {
    //회원(등록)
    MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest);
    LoginMember doLogin(String mbEmail, String mbPassword);
    MemberResponse getMember(long mbNo);
    MemberResponse getMemberByEmail(String mbEmail);
}
