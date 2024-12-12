package com.nhnacademy.blog.auth;

public class MemberThreadLocal {
    private static final ThreadLocal<Long> memberNoLocal = new ThreadLocal<>();

    public static Long getMemberNo() {
        return memberNoLocal.get();
    }

    public static void setMemberNo(Long memberNo) {
        memberNoLocal.set(memberNo);
    }

    public static void removeMemberNo() {
        memberNoLocal.remove();
    }
}
