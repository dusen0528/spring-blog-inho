package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.common.exception.UnauthorizedException;
import com.nhnacademy.blog.common.security.PasswordEncoder;
import com.nhnacademy.blog.common.security.impl.BCryptPasswordEncoder;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.*;
import com.nhnacademy.blog.member.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j

/**
 * ExtendWith(MockitoExtension.class)
 * JUnit 5에서 Mockito를 사용하려면 @ExtendWith(MockitoExtension.class) 애너테이션을 사용해야 합니다.
 * 이 애너테이션은 JUnit 5 테스트 실행 시 MockitoExtension을 활성화하여 Mockito의 기능을 지원하도록 해줍니다.
 * 이 애너테이션을 사용하면, @Mock, @InjectMocks, @Spy 등과 같은 Mockito 관련 애너테이션들이 자동으로 초기화됩니다.
 * 즉, MockitoExtension은 다음을 제공합니다:
 *  1. @Mock으로 선언된 객체를 테스트 클래스의 필드에 자동으로 주입.
 *  2. @InjectMocks로 선언된 클래스에서 @Mock으로 선언된 객체를 자동으로 주입.
 *  3. @Spy로 선언된 객체의 실제 동작을 부분적으로 감시하고 수정 가능.
 *  4. Mockito의 모든 메소드가 테스트 실행 전에 설정되어, Mockito의 mock 객체들을 제대로 사용할 수 있게 만듭니다.
 */
@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    /**
     * @Mock 애너테이션은 필드에 대한 mock 객체를 생성하여 테스트에 사용할 수 있도록 설정합니다.
     * `@Mock`은 테스트 대상 클래스의 의존성을 가짜(mock) 객체로 만들어서, 테스트 중 실제 객체를 사용하지 않고 가짜 객체로 테스트를 진행하게 합니다.
     * 예를 들어, 이 경우 `MemberRepository`는 실제 데이터베이스와의 상호작용을 하지 않고, 가짜 객체로 테스트가 이루어집니다.
     * 이렇게 하면, 데이터베이스와의 연결을 위한 복잡한 설정이나 외부 시스템에 의존하지 않고 빠르고 격리된 테스트를 할 수 있습니다.
     * Mockito는 이 필드를 자동으로 초기화하며, 테스트 중에 해당 mock 객체의 메서드를 설정하거나 호출하여, 객체 간의 상호작용을 검증할 수 있습니다.
     */
    @Mock
    MemberRepository memberRepository;

    /**
     * @InjectMocks 애너테이션은 테스트 대상 클래스에 대한 의존성을 주입할 때 사용됩니다.
     * 이 애너테이션은 `@Mock`으로 생성된 객체들을 테스트 클래스에 자동으로 주입해줍니다.
     * 예를 들어, `MemberServiceImpl` 클래스는 `@Mock`으로 설정된 `memberRepository` 객체를 생성자나 필드 주입 방식으로 자동으로 주입받게 됩니다.
     * `@InjectMocks`를 사용하면, `memberService` 객체가 `memberRepository`를 의존성으로 주입받아 실제 테스트 대상이 되는 서비스 클래스를 테스트할 수 있습니다.
     * 이 방식은 객체 간의 의존성 주입을 수동으로 설정할 필요 없이 Mockito가 자동으로 처리해주므로, 더 깔끔하고 효율적인 테스트 코드를 작성할 수 있게 해줍니다.
     */
    @InjectMocks
    MemberServiceImpl memberService;

    /**
     * `@Spy`는 객체의 실제 동작을 유지하면서, 그 동작을 부분적으로 검증하거나 수정할 수 있게 해줍니다.
     * 즉, `@Spy`는 해당 객체가 실제로 동작하는 것을 유지하되, 특정 메서드 호출에 대해서만 Mockito를 사용해 동작을 변경하거나 검증할 수 있습니다.
     * 예를 들어, `passwordEncoder`는 `BCryptPasswordEncoder`의 실제 구현체로, 암호화 로직은 실제로 수행됩니다.
     * 하지만 `@Spy`를 사용하면, 이 객체의 메서드가 호출될 때 어떤 동작을 했는지 확인하거나, 특정 메서드를 스텁(stub)하여 예상되는 값을 반환하도록 설정할 수 있습니다.
     * `@Spy`는 실제 객체를 기반으로 하므로, `@Mock`처럼 모든 메서드를 가짜로 만드는 것이 아니라 실제 동작과 일부 검증을 병행할 수 있습니다.
     */

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    @DisplayName("회원등록")
    void registerMember() {

        MemberRegisterRequest memberRegisterRequest = new MemberRegisterRequest(
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
                "marco@nhnacademy.com",
                "마르코",
                "a123456789!",
                "01012345678"
        );

        Mockito.when(memberRepository.existsByMbEmail(Mockito.anyString())).thenReturn(true);

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
            memberService.getMember(1L);
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
    @DisplayName("로그인")
    void doLogin(){
        LoginRequest loginRequest = new LoginRequest("marco@nhnacademy.com","1234567890a!");

        Member member = Member.ofExistingMember(1L,
                "marco@nhnacademy.com",
                "마르코",
                passwordEncoder.encode(loginRequest.getPassword()),
                "01012345678",
                LocalDateTime.now().minusDays(-30),
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(30)
        );

        Mockito.when(memberRepository.findByMbEmail(Mockito.anyString())).thenReturn(Optional.of(member));

        LoginMember loginMember = memberService.doLogin(loginRequest);

        Assertions.assertAll(
                ()->{
                    Assertions.assertNotNull(loginMember);
                },
                ()->{
                    Assertions.assertEquals(1L, loginMember.getMbNo());
                },
                ()->{
                    Assertions.assertEquals("marco@nhnacademy.com", loginMember.getMbEmail());
                },
                ()->{
                    Assertions.assertEquals("마르코",loginMember.getMbName());
                }
        );
        Mockito.verify(memberRepository, Mockito.times(1)).findByMbEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("로그인-실폐-회원이 존재하지 않음 : 401")
    void doLogin_exception_case1() {
        LoginRequest loginRequest = new LoginRequest("marco@nhnacademy.com","1234567890a!");

        Mockito.when(memberRepository.findByMbEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnauthorizedException.class,()->{
            memberService.doLogin(loginRequest);
        });

    }

    @Test
    @DisplayName("로그인-실폐-회원은 존재하지만 비밀번호 불일치 : 401")
    void doLogin_exception_case2() {
        LoginRequest loginRequest = new LoginRequest("marco@nhnacademy.com","1234567890a!");

        Mockito.when(memberRepository.findByMbEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(UnauthorizedException.class,()->{
            memberService.doLogin(loginRequest);
        });

        Mockito.verify(memberRepository, Mockito.times(1)).findByMbEmail(Mockito.anyString());
    }

    @Test
    @DisplayName("passwordEncoder - matches")
    void passwordEncoder_validate() {
        String rawPassword = "a1234567890!";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        log.debug("rawPassword: {}", rawPassword);
        log.debug("encodedPassword: {}", encodedPassword);
        boolean actual = passwordEncoder.matches(rawPassword, encodedPassword);
        Assertions.assertTrue(actual);
    }

}