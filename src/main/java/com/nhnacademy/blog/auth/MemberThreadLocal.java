package com.nhnacademy.blog.auth;

import java.util.Objects;

public final class MemberThreadLocal {
    private static final ThreadLocal<Long> memberNoLocal = new ThreadLocal<>();

    private MemberThreadLocal() {
        throw new IllegalStateException("Utility class");
    }

    public static Long getMemberNo() {
        if (Objects.isNull(memberNoLocal.get())){
            return Long.MAX_VALUE;
        }
        return memberNoLocal.get();
    }

    public static void setMemberNo(Long memberNo) {
        memberNoLocal.set(memberNo);
    }

    public static void removeMemberNo() {
        memberNoLocal.remove();
    }
}
