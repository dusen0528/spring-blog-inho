package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO#3-1 JdbcMemberRepositoryTest 참고하여 모든 Repository의 테스트 환경 설정 합니다.
 */

@Slf4j
/**
 * TODO#3-1-1 Spring 기반 테스트 환경 구성
 * @ContextConfiguration: 테스트 클래스에서 사용할 Spring 설정 클래스를 지정합니다.
 * 이 예제에서는 ApplicationConfig.class를 지정하여,
 * 테스트 실행 시 해당 클래스에서 설정된 Spring Bean들을 사용할 수 있도록 합니다.
 */
@ContextConfiguration(classes = {ApplicationConfig.class})

/**
 * TODO#3-1-2 JUnit 5에서 Spring 테스트 기능 활성화
 * @ExtendWith(SpringExtension.class): JUnit 5에서 Spring 테스트 기능을 활성화하는 어노테이션입니다.
 * 이 어노테이션을 사용하면 Spring TestContext Framework가 활성화되어 Spring의 테스트 환경을 설정할 수 있습니다.
 * SpringExtension은 JUnit 5의 Extension API를 구현한 클래스로, 이를 통해 Spring ApplicationContext와 연동할 수 있습니다.
 * SpringExtension은 테스트 클래스의 라이프사이클에 맞춰 Spring Context를 관리하고, 의존성 주입을 처리하며,
 * 트랜잭션 관리를 포함한 Spring 테스트 기능을 제공합니다.
 *
 * 이 어노테이션을 사용하면 Spring의 @Autowired, @MockBean, @Value 등과 같은 의존성 주입을 사용할 수 있으며,
 * Spring의 @Transactional이나 @Before, @After와 같은 다양한 테스트 기능도 지원됩니다.
 * 즉, SpringExtension을 통해 Spring의 IoC 컨테이너와 테스트 환경을 결합하여,
 * Spring 애플리케이션의 구성 요소들이 올바르게 작동하는지 확인할 수 있습니다.
 */
@ExtendWith(SpringExtension.class)

/**
 * TODO#3-1-3 테스트 환경에서의 트랜잭션 처리
 * @Transactional: 이 어노테이션은 테스트 메소드가 실행될 때 트랜잭션을 시작하고, 메소드 실행 후 자동으로 롤백되도록 설정합니다.
 * 주로 데이터베이스의 상태를 변경하는 테스트에서 사용되며, 테스트가 끝난 후 데이터베이스에 변경 사항을 남기지 않도록 합니다.
 */
@Transactional

class JdbcMemberRepositoryTest {
    //TODO#3-1-4 Spring Context로부터 memberRepository 주입합니다.
    @Autowired
    MemberRepository memberRepository;

    /**
     * TODO#3-1-5 Rollback 관리
     *  - @Rollback(value=true)  =  test method 실행 후 rollback 됩니다.
     *  - @Rollback(value=false) =  test method 실행 후 rollback 되지 않습니다.
     *  - @Rollback 에너테이션을 사용하지 않더라도 테스트에서는 @Transactional 때문에 rollback 됩니다.
     */
    @Rollback(value = true)
    @Test
    @DisplayName("회원등록")
    void save() {

        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        Member member2 = Member.ofNewMember("test@nhnacademy.com","테스트","12345","01011112222");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Optional<Member> actualOptionalMember1 = memberRepository.findByMbNo(member1.getMbNo());
        Optional<Member> actualOptionalMember2 = memberRepository.findByMbNo(member1.getMbNo());

        Assertions.assertTrue(actualOptionalMember1.isPresent());
        Assertions.assertTrue(actualOptionalMember2.isPresent());

        log.debug("member1: {}", actualOptionalMember1.get());
        log.debug("member2: {}", actualOptionalMember2.get());

        Assertions.assertAll(
                ()->Assertions.assertNotNull(actualOptionalMember1.get()),
                ()->Assertions.assertEquals(member1.getMbEmail(), actualOptionalMember1.get().getMbEmail()),
                ()->Assertions.assertEquals(member1.getMbName(), actualOptionalMember1.get().getMbName()),
                ()->Assertions.assertEquals(member1.getMbPassword(), actualOptionalMember1.get().getMbPassword()),
                ()->Assertions.assertEquals(member1.getMbMobile(), actualOptionalMember1.get().getMbMobile()),
                ()->Assertions.assertNotNull(actualOptionalMember1.get().getCreatedAt()),

                ()->Assertions.assertNotNull(actualOptionalMember2.get()),
                ()->Assertions.assertEquals(member1.getMbEmail(), actualOptionalMember2.get().getMbEmail()),
                ()->Assertions.assertEquals(member1.getMbName(), actualOptionalMember2.get().getMbName()),
                ()->Assertions.assertEquals(member1.getMbPassword(), actualOptionalMember2.get().getMbPassword()),
                ()->Assertions.assertEquals(member1.getMbMobile(), actualOptionalMember2.get().getMbMobile()),
                ()->Assertions.assertNotNull(actualOptionalMember2.get().getCreatedAt())
        );

    }

    @Test
    @DisplayName("회원정보 수정{이메일,이름,연락처")
    void update() {
        Member member3 = Member.ofNewMember("member3@nhnacademy.com","회원3","12345","01033333333");
        memberRepository.save(member3);

        MemberUpdateRequest memberUpdateRequest = new MemberUpdateRequest(
                member3.getMbNo(),
                "member3@gmail.com",
                "NHN아카데미3",
                "01033334444"
        );

        memberRepository.update(memberUpdateRequest);

        Optional<Member> actualOptionalMember3 = memberRepository.findByMbNo(member3.getMbNo());

        Assertions.assertAll(
            ()-> Assertions.assertEquals("member3@gmail.com",actualOptionalMember3.get().getMbEmail()),
            ()-> Assertions.assertEquals("NHN아카데미3",actualOptionalMember3.get().getMbName()),
            ()-> Assertions.assertEquals("01033334444",actualOptionalMember3.get().getMbMobile())
        );
    }


    @Test
    @DisplayName("회원삭제")
    void delete() {
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);
        log.debug("member1:{}",member1);

        memberRepository.deleteByMbNo(member1.getMbNo());
        boolean actual = memberRepository.existsByMbNo(member1.getMbNo());
        log.debug("member with id [{}] has bean deleted:{}",member1.getMbNo(),actual);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword(){
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);
        log.debug("member1:{}",member1);
        String mbPassword = "changePassword";
        memberRepository.updatePassword(member1.getMbNo(),mbPassword);

        Optional<Member> dbMemberOptional = memberRepository.findByMbNo(member1.getMbNo());
        log.debug("changed mbPassword:{}",dbMemberOptional.get().getMbPassword());
        Assertions.assertEquals(mbPassword,dbMemberOptional.get().getMbPassword());
    }

    @Test
    @DisplayName("회원조회-by-회원번호")
    void findByMbNo() {

        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);

        Optional<Member> actualOptional = memberRepository.findByMbNo(member1.getMbNo());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(actualOptional.get()),
                ()->Assertions.assertEquals(member1.getMbNo(), actualOptional.get().getMbNo()),
                ()->Assertions.assertEquals(member1.getMbEmail(), actualOptional.get().getMbEmail()),
                ()->Assertions.assertEquals(member1.getMbName(), actualOptional.get().getMbName()),
                ()->Assertions.assertEquals(member1.getMbPassword(), actualOptional.get().getMbPassword()),
                ()->Assertions.assertEquals(member1.getMbMobile(), actualOptional.get().getMbMobile()),
                ()->Assertions.assertNotNull(actualOptional.get().getCreatedAt())
        );

    }

    @Test
    @DisplayName("회원조회-by-email")
    void findByMbEmail() {
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);

        Optional<Member> actualOptional = memberRepository.findByMbNo(member1.getMbNo());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(actualOptional.get()),
                ()->Assertions.assertEquals(member1.getMbNo(), actualOptional.get().getMbNo()),
                ()->Assertions.assertEquals(member1.getMbEmail(), actualOptional.get().getMbEmail()),
                ()->Assertions.assertEquals(member1.getMbName(), actualOptional.get().getMbName()),
                ()->Assertions.assertEquals(member1.getMbPassword(), actualOptional.get().getMbPassword()),
                ()->Assertions.assertEquals(member1.getMbMobile(), actualOptional.get().getMbMobile()),
                ()->Assertions.assertNotNull(actualOptional.get().getCreatedAt())
        );
    }

    @Test
    @DisplayName("회원조회-by-mobile")
    void findByMbMobile() {
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);

        Optional<Member> actualOptional = memberRepository.findByMbMobile(member1.getMbMobile());

        Assertions.assertAll(
                ()->Assertions.assertNotNull(actualOptional.get()),
                ()->Assertions.assertEquals(member1.getMbNo(), actualOptional.get().getMbNo()),
                ()->Assertions.assertEquals(member1.getMbEmail(), actualOptional.get().getMbEmail()),
                ()->Assertions.assertEquals(member1.getMbName(), actualOptional.get().getMbName()),
                ()->Assertions.assertEquals(member1.getMbPassword(), actualOptional.get().getMbPassword()),
                ()->Assertions.assertEquals(member1.getMbMobile(), actualOptional.get().getMbMobile()),
                ()->Assertions.assertNotNull(actualOptional.get().getCreatedAt())
        );
    }

    @Test
    @DisplayName("회원번호로 회원존제여부 : true")
    void existsByMbNo() {
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);

        boolean actual = memberRepository.existsByMbNo(member1.getMbNo());
        assertTrue(actual);
    }
    @Test
    @DisplayName("회원번호로 회원존제여부 : false")
    void notExistsByMbNo() {
        boolean actual = memberRepository.existsByMbNo(1L);
        assertFalse(actual);
    }

    @Test
    @DisplayName("회원존재여부-by-email : true")
    void existsByMbEmail() {
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);

        boolean actual = memberRepository.existsByMbEmail(member1.getMbEmail());
        assertTrue(actual);
    }

    @Test
    @DisplayName("회원존재여부-by-email : false")
    void notExistsByMbEmail() {
        boolean actual = memberRepository.existsByMbEmail("something@email.com");
        assertFalse(actual);
    }

    @Test
    @DisplayName("회원존재여부-by-mobile : true")
    void existsByMbMobile() {
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);
        log.debug("member1: {}", member);
        boolean actual = memberRepository.existsByMbMobile(member.getMbMobile());
        assertTrue(actual);
    }
    @Test
    @DisplayName("회원존재여부-by-mobile : false")
    void notExistsByMbMobile() {
        boolean actual = memberRepository.existsByMbMobile("01012345678");
        assertFalse(actual);
    }

    @Test
    @DisplayName("회원 탈퇴여부: true")
    void isMemberWithdrawn_true(){
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);
        memberRepository.updateWithdrawalAt(member.getMbNo(),LocalDateTime.now());

        boolean actual = memberRepository.isMemberWithdrawn(member.getMbNo());
        assertTrue(actual);
    }

    @Test
    @DisplayName("회원 탈퇴여부: false")
    void isMemberWithdrawn_false(){
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        boolean actual = memberRepository.isMemberWithdrawn(member.getMbNo());
        assertFalse(actual);
    }

    @Test
    @DisplayName("회원탈퇴:탈퇴일자 수정")
    void updateWithdrawalAt() {
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);
        log.debug("탈퇴전 : member:{}",member1.getWithdrawalAt());

        memberRepository.updateWithdrawalAt(member1.getMbNo(), LocalDateTime.now());

        Member dbMember = memberRepository.findByMbNo(member1.getMbNo()).orElse(null);
        Assertions.assertNotNull(dbMember.getMbNo());
        log.debug("탈퇴 후 : member:{}",dbMember.getWithdrawalAt());
    }
}