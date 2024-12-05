package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberUpdateRequestDto;
import com.nhnacademy.blog.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JdbcMemberRepositoryTest {
    static MemberRepository memberRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        memberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
    }

    @BeforeEach
    void setUp(){
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown(){
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("회원등록")
    void save() {

        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        Member member2 = Member.ofNewMember("test@nhnacademy.com","테스트","12345","01011112222");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Optional<Member> actualOptionalMember1 = memberRepository.findByMbNo(member1.getMbNo());
        Optional<Member> actualOptionalMember2 = memberRepository.findByMbNo(member1.getMbNo());

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

        MemberUpdateRequestDto memberUpdateRequestDto = new MemberUpdateRequestDto(
                member3.getMbNo(),
                "member3@gmail.com",
                "NHN아카데미3",
                "01033334444"
        );

        memberRepository.update(memberUpdateRequestDto);

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

        memberRepository.delete(member1.getMbNo());
        boolean actual = memberRepository.existsByMbNo(member1.getMbNo());
        log.debug("member with id [{}] has bean deleted:{}",member1.getMbNo(),actual);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword(){
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);
        log.debug("member1:{}",member1);
        String mbPassword = "changePassword";
        memberRepository.changePassword(member1.getMbNo(),mbPassword);

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
        boolean actual = memberRepository.existsByMbNo(1l);
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
        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member1);
        log.debug("member1: {}", member1);
        boolean actual = memberRepository.existsByMbMobile(member1.getMbMobile());
        assertTrue(actual);
    }
    @Test
    @DisplayName("회원존재여부-by-mobile : false")
    void notExistsByMbMobile() {
        boolean actual = memberRepository.existsByMbMobile("01012345678");
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