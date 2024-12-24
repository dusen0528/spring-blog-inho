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

@Slf4j
@ContextConfiguration(classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@Transactional
class JdbcMemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;

    //@Rollback 에너테이션을 사용하지 않더라도 테스트에서는 @Transactional 때문에 rollback 됩니다.
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