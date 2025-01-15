package com.nhnacademy.blog.member.auth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class LoginMember {

    private Long mbNo;
    private String mbEmail;
    private String mbName;
    private LocalDateTime loginAt;

    public static LoginMember of(Long mbNo, String mbEmail, String mbName) {
        return new LoginMember(mbNo, mbEmail, mbName, LocalDateTime.now());
    }
}
