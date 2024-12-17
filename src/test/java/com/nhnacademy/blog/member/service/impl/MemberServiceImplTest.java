package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberPasswordUpdateRequest;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.service.MemberService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
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

        Mockito.doAnswer(invocation -> {
            Member paramMember = invocation.getArgument(0);
            Field field = Member.class.getDeclaredField("mbNo");
            field.setAccessible(true);
            field.set(paramMember, 1L);
            log.debug("paramMember: {}", paramMember);
            return null;
        }).when(memberRepository).save(Mockito.any(Member.class));

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        "password",
                        "01012345678",
                        LocalDateTime.now(),
                        null,
                        null
                )
        );

        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);

        MemberResponse memberResponse = memberService.registerMember(memberRegisterRequest);

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbMobile(Mockito.anyString());
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbEmail(Mockito.anyString());
        Mockito.verify(memberRepository, Mockito.times(1)).save(Mockito.any(Member.class));

        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(1L,memberResponse.getMbNo());
                },
                ()->{
                    Assertions.assertEquals("marco@nhnacademy.com",memberResponse.getMbEmail());
                },
                ()->{
                    Assertions.assertEquals("마르코",memberResponse.getMbName());
                },
                ()->{
                    Assertions.assertEquals("01012345678",memberResponse.getMbMobile());
                },
                ()->{
                    Assertions.assertNotNull(memberResponse.getCreatedAt());
                }
        );
    }

    @Test
    @DisplayName("회원등록 - 이메일중복 ")
    void registerMember_exception_case1() {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
                1L,
                "marco@nhnacademy.com",
                "마르코",
                "a123456789!",
                "01012345678"
        );

        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(true);
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(ConflictException.class,()->{
            memberService.registerMember(memberRegisterRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbEmail(Mockito.anyString());
        Mockito.verify(memberRepository,Mockito.never()).save(Mockito.any(Member.class));
    }

    @Test
    @DisplayName("회원등록 - 모바일중복 ")
    void registerMember_exception_case2() {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
                1L,
                "marco@nhnacademy.com",
                "마르코",
                "a123456789!",
                "01012345678"
        );

        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(false);
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class,()->{
            memberService.registerMember(memberRegisterRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbMobile(Mockito.anyString());
        Mockito.verify(memberRepository,Mockito.never()).save(Mockito.any(Member.class));
    }

    @Test
    @DisplayName("회원정보-수정-이름 변경")
    void updateMember() {

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        "password",
                        "01012345678",
                        LocalDateTime.now().minusDays(10),
                        LocalDateTime.now(),
                        null
                )
        );

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(1L,"marco@nhnacademy.com","NHN아카데미","01012345678");
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);
        //회원존재여부 체크
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원탈퇴여부 체크
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);
        //회원이메일 중복체크
        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(false);
        //회원모바일연락처 중복체크
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(false);

        MemberResponse memberResponse = memberService.updateMember(memberUpdateRequest);
        log.debug("memberResponse: {}", memberResponse);

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).isMemberWithdrawn(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.atLeast(1)).findByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).update(Mockito.any(MemberUpdateRequest.class));


        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(1L,memberResponse.getMbNo());
                },
                ()->{
                    Assertions.assertEquals("marco@nhnacademy.com",memberResponse.getMbEmail());
                },
                ()->{
                    Assertions.assertEquals("마르코",memberResponse.getMbName());
                },
                ()->{
                    Assertions.assertEquals("01012345678",memberResponse.getMbMobile());
                },
                ()->{
                    Assertions.assertNotNull(memberResponse.getCreatedAt());
                },
                ()->{
                    Assertions.assertNotNull(memberResponse.getUpdatedAt());
                }
        );

    }

    @Test
    @DisplayName("회원정보-수정-이름,이메일(중복)")
    void updateMember_exception_case1() {

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        "password",
                        "01012345678",
                        LocalDateTime.now(),
                        null,
                        null
                )
        );

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(1L,"nhn-academy-marco@nhnacademy.com","NHN아카데미","01012345678");
        //회원조회
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);
        //회원존재여부 체크
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원탈퇴여부 체크
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);
        //회원이메일 중복체크
        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(true);
        //회원모바일연락처 중복체크
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(false);

        Assertions.assertThrows(ConflictException.class,()->{
            memberService.updateMember(memberUpdateRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbEmail(Mockito.anyString());
        Mockito.verify(memberRepository, Mockito.times(1)).findByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.never()).update(Mockito.any(MemberUpdateRequest.class));

    }

    @Test
    @DisplayName("회원정보-수정-이름,모바일 연락처(중복)")
    void updateMember_exception_case2() {

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        "password",
                        "01012345678",
                        LocalDateTime.now(),
                        null,
                        null
                )
        );

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(1L,"nhn-academy-marco@nhnacademy.com","NHN아카데미","01011111111");

        //회원조회
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);
        //회원이메일 중복체크
        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(false);
        //회원모바일연락처 중복체크
        Mockito.when(memberRepository.existsByMbMobile(Mockito.anyString())).thenReturn(true);

        Assertions.assertThrows(ConflictException.class,()->{
            memberService.updateMember(memberUpdateRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbMobile(Mockito.anyString());
        Mockito.verify(memberRepository, Mockito.times(1)).findByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.never()).update(Mockito.any(MemberUpdateRequest.class));

    }

    @Test
    @DisplayName("회원정보-수정 - 탈퇴한 회원")
    void updateMember_exception_case3() {

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        "password",
                        "01012345678",
                        LocalDateTime.now(),
                        null,
                        LocalDateTime.now()
                )
        );

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(1L,"nhn-academy-marco@nhnacademy.com","NHN아카데미","01012345678");

        //탈퇴한 회원 체크
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(true);

        Assertions.assertThrows(NotFoundException.class,()->{
            memberService.updateMember(memberUpdateRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).isMemberWithdrawn(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.never()).update(Mockito.any(MemberUpdateRequest.class));

    }

    @Test
    @DisplayName("회원탈퇴")
    void withdrawal() {

        //회원존재여부?
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //탈퇴한 회원인지?
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        memberService.withdrawalMember(1L);

        Mockito.verify(memberRepository, Mockito.times(1)).isMemberWithdrawn(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).updateWithdrawalAt(Mockito.anyLong(),Mockito.any(LocalDateTime.class));
    }

    @Test
    @DisplayName("회원탈퇴-존재하지 않는 회원")
    void withdrawalMember_exception_case1() {

        //회원존재여부?
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(NotFoundException.class,()->{
            memberService.withdrawalMember(1L);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository,Mockito.never()).updateWithdrawalAt(Mockito.anyLong(),Mockito.any(LocalDateTime.class));
    }

    @Test
    @DisplayName("회원탈퇴-이미 탈퇴한 회원")
    void withdrawalMember_exception_case2() {

        //회원존재여부?
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원탈퇴여부?
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(true);

        Assertions.assertThrows(NotFoundException.class,()->{
            memberService.withdrawalMember(1L);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).isMemberWithdrawn(Mockito.anyLong());
        Mockito.verify(memberRepository,Mockito.never()).updateWithdrawalAt(Mockito.anyLong(),Mockito.any(LocalDateTime.class));
    }


    @Test
    @DisplayName("회원조회")
    void getMember() {
        //given
        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        "password",
                        "01012345678",
                        LocalDateTime.now().minusDays(-30),
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now().plusDays(30)
                )
        );

        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);
        //when
        MemberResponse memberResponse = memberService.getMember(1L);

        //then
        Mockito.verify(memberRepository, Mockito.atLeast(0)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.atLeast(1)).findByMbNo(Mockito.anyLong());

        Assertions.assertAll(
                ()->Assertions.assertEquals(memberOptional.get().getMbNo(), memberResponse.getMbNo()),
                ()->Assertions.assertEquals(memberOptional.get().getMbEmail(), memberResponse.getMbEmail()),
                ()->Assertions.assertEquals(memberOptional.get().getMbName(), memberResponse.getMbName()),
                ()->Assertions.assertEquals(memberOptional.get().getMbMobile(), memberResponse.getMbMobile()),
                ()->Assertions.assertEquals(memberOptional.get().getCreatedAt(), memberResponse.getCreatedAt()),
                ()->Assertions.assertEquals(memberOptional.get().getUpdatedAt(), memberResponse.getUpdatedAt()),
                ()->Assertions.assertEquals(memberOptional.get().getWithdrawalAt(), memberResponse.getWithdrawalAt())
        );
    }

    @Test
    @DisplayName("회원조회:존재하지 않는 회원 : 404")
    void getMember_exception_case1() {
        //given
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(Optional.empty());

        //when
        Assertions.assertThrows(NotFoundException.class,()->{
            MemberResponse memberResponse = memberService.getMember(1L);
        });

        //then
        Mockito.verify(memberRepository, Mockito.times(1)).findByMbNo(Mockito.anyLong());
    }

    @Test
    void changePassword() {
        //given
        MemberPasswordUpdateRequest memberPasswordUpdateRequest = new MemberPasswordUpdateRequest(1L,"oldPassword","newPassword");

        String dbPassword = BCrypt.hashpw(memberPasswordUpdateRequest.getOldPassword(), BCrypt.gensalt());

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        dbPassword,
                        "01012345678",
                        LocalDateTime.now().minusDays(-30),
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now().plusDays(30)
                )
        );

        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);

        //when
        memberService.changePassword(memberPasswordUpdateRequest);

        //then
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).findByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).updatePassword(Mockito.anyLong(),Mockito.anyString());
    }

    @Test
    @DisplayName("변경전 password와 데이터베이스 password 불일치")
    void changePassword_exception_case1() {
        //given
        MemberPasswordUpdateRequest memberPasswordUpdateRequest = new MemberPasswordUpdateRequest(1L,"oldPassword","newPassword");

        String dbPassword = BCrypt.hashpw("something", BCrypt.gensalt());

        Optional<Member> memberOptional = Optional.of(
                Member.ofExistingMember(1L,
                        "marco@nhnacademy.com",
                        "마르코",
                        dbPassword,
                        "01012345678",
                        LocalDateTime.now().minusDays(-30),
                        LocalDateTime.now().plusDays(5),
                        LocalDateTime.now().plusDays(30)
                )
        );

        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        Mockito.when(memberRepository.findByMbNo(Mockito.anyLong())).thenReturn(memberOptional);

        //when
        Assertions.assertThrows(BadRequestException.class,()->{
            memberService.changePassword(memberPasswordUpdateRequest);
        });

        //then
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.times(1)).findByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.never()).updatePassword(Mockito.anyLong(),Mockito.anyString());
    }

    @Test
    @DisplayName("비밀번호변경 - 존재하지 않는 회원")
    void changePassword_exception_case2() {
        //given
        MemberPasswordUpdateRequest memberPasswordUpdateRequest = new MemberPasswordUpdateRequest(1L,"oldPassword","newPassword");

        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(false);

        //when
        Assertions.assertThrows(NotFoundException.class,()->{
            memberService.changePassword(memberPasswordUpdateRequest);
        });

        //then
        Mockito.verify(memberRepository, Mockito.times(1)).existsByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.never()).findByMbNo(Mockito.anyLong());
        Mockito.verify(memberRepository, Mockito.never()).updatePassword(Mockito.anyLong(),Mockito.anyString());
    }


    @Test
    @DisplayName("passwordEncoder - test")
    void passwordEncoder_validate() {
        String rawPassword = "oldPassword";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

        // 비밀번호 검증
        boolean actual = BCrypt.checkpw(rawPassword, hashedPassword);
        Assertions.assertTrue(actual);
    }
}