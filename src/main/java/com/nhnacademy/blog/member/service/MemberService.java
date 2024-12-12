package com.nhnacademy.blog.member.service;

import com.nhnacademy.blog.member.dto.MemberPasswordUpdateRequest;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;

public interface MemberService {

    //회원(등록)
    void registerMember(MemberRegisterRequest memberRegisterRequest);
    //회원정보(수정)
    void updateMember(MemberUpdateRequest memberUpdateRequest);
    //회원(탈퇴)
    void withdrawalMember(long mbNo);
    //회원조회
    MemberResponse getMember(long mbNo);
    //비밀번호 변경
    void changePassword(MemberPasswordUpdateRequest memberPasswordUpdateRequest);

}
