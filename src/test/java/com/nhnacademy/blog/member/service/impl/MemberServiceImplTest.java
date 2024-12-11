package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.service.MemberService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class MemberServiceImplTest {

    MemberRepository memberRepository;
    MemberService memberService;

    @BeforeEach
    void setUp() {
        memberRepository = Mockito.mock(MemberRepository.class);
        memberService = new MemberServiceImpl(memberRepository);
    }

    @Test
    @DisplayName("회원등록")
    void registerMember() {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
                1L,
                "marco@nhnacademy.com",
                "마르코",
                "a123456789!",
                "01012345678"
        );

        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(false);

        memberService.registerMember(memberRegisterRequest);

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbMobile(Mockito.anyString());
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbEmail(Mockito.anyString());
        Mockito.verify(memberRepository, Mockito.times(1)).save(Mockito.any(Member.class));
    }

    @Test
    @DisplayName("회원등록 - 이메일중복 ")
    void registerMember_duplicate_email() {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
                1L,
                "marco@nhnacademy.com",
                "마르코",
                "a123456789!",
                "01012345678"
        );

        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(true);
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(BadRequestException.class,()->{
            memberService.registerMember(memberRegisterRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbEmail(Mockito.anyString());
        Mockito.verify(memberRepository,Mockito.never()).save(Mockito.any(Member.class));
    }

    @Test
    @DisplayName("회원등록 - 모바일중복 ")
    void registerMember_duplicate_mobile() {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
                1L,
                "marco@nhnacademy.com",
                "마르코",
                "a123456789!",
                "01012345678"
        );

        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(BadRequestException.class,()->{
            memberService.registerMember(memberRegisterRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbMobile(Mockito.anyString());
        Mockito.verify(memberRepository,Mockito.never()).save(Mockito.any(Member.class));
    }

    @Test
    void updateMember() {

    }

    @Test
    void withdrawalMember() {

    }

    @Test
    void getMember() {

    }

    @Test
    void changePassword() {

    }
}