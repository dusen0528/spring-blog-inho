package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.JpaMemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class JpaMemberRepositoryTest {

    /**
     * @PersistenceContext는 Java Persistence API(JPA)에서 EntityManager를 주입하기 위해 사용하는 애노테이션입니다.
     * 이 애노테이션을 사용하면 Spring이나 다른 Java EE 환경에서 EntityManager를 주입할 수 있으며,
     * 이를 통해 데이터베이스 작업을 수행할 수 있습니다.
     * Spring 기반으로 개발할거면.. @Autowired를 사용해도 됨
     */

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    JpaMemberRepository memberRepository;

    @Test
    @Transactional
    @DisplayName("회원등록")
    void save(){

        Member member= Member.ofNewMember("marco@nhnacademy.com",  "marco","password","01012345678");
        entityManager.persist(member);

        //flush()는 영속성 컨텍스트의 변경 사항을 데이터베이스에 강제로 반영합니다.
        entityManager.flush();

        log.debug("member : {}", member);

        Member newMember = entityManager.find(Member.class, member.getMbNo());
        log.debug("newMember: {}", newMember);

        Assertions.assertNotNull(newMember);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(member.getMbNo(), newMember.getMbNo());
                },
                ()->{
                    Assertions.assertEquals("marco@nhnacademy.com", newMember.getMbEmail());
                },
                ()->{
                    Assertions.assertEquals("marco", newMember.getMbName());
                },
                ()->{
                    Assertions.assertEquals("password", newMember.getMbPassword());
                },
                ()->{
                    Assertions.assertNotNull(newMember.getCreatedAt());
                }
        );
    }

    @Test
    @DisplayName("회원정보 수정{이메일,이름,연락처")
    void update() {
        Member member = Member.ofNewMember("member3@nhnacademy.com","회원3","12345","01033333333");
        memberRepository.saveAndFlush(member);
        long memberNo = member.getMbNo();


        Optional<Member> dbMemberOptional = memberRepository.findByMbNo(memberNo);
        Member dbMember = dbMemberOptional.get();
        dbMember.update("member3@gmail.com", "NHN아카데미3", "01033334444" );
        memberRepository.saveAndFlush(dbMember);

        Optional<Member> actualOptionalMember = memberRepository.findByMbNo(memberNo);
        Assertions.assertTrue(actualOptionalMember.isPresent());
        Assertions.assertAll(
                ()-> Assertions.assertEquals("member3@gmail.com",actualOptionalMember.get().getMbEmail()),
                ()-> Assertions.assertEquals("NHN아카데미3",actualOptionalMember.get().getMbName()),
                ()-> Assertions.assertEquals("01033334444",actualOptionalMember.get().getMbMobile()),
                ()-> Assertions.assertNotNull(actualOptionalMember.get().getCreatedAt()),
                ()-> Assertions.assertNotNull(actualOptionalMember.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("회원삭제")
    void delete() {

        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.saveAndFlush(member);
        long memberId = member.getMbNo();
        log.debug("member:{}",member);

        memberRepository.deleteById(memberId);
        memberRepository.flush();

        boolean actual = memberRepository.existsByMbNo(memberId);
        log.debug("member with id [{}] has bean deleted:{}",memberId,actual);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword() {

        Member member= Member.ofNewMember("marco@nhnacademy.com",  "marco","password","01012345678");
        memberRepository.saveAndFlush(member);
        log.debug("member : {}", member);

        /**
         * saveAndFlush()
         * - 변경 사항 반영: flush는 영속성 컨텍스트에 있는 모든 변경 사항을 데이터베이스에 반영합니다.
         *   즉, INSERT, UPDATE, DELETE와 같은 SQL 쿼리가 실행됩니다.
         *
         * 영속성 컨텍스트 유지: flush는 영속성 컨텍스트에 있는 엔티티를 그대로 유지합니다.
         * 1차 캐시의 엔티티는 여전히 존재합니다.
         */

        Optional<Member> memberOptional = memberRepository.findByMbNo(member.getMbNo());
        Assertions.assertTrue(memberOptional.isPresent());
        Member dbMember = memberOptional.get();
        dbMember.changePassword("newPassword");
        memberRepository.saveAndFlush(dbMember);

        Optional<Member> updatedMemberOptional = memberRepository.findByMbNo(member.getMbNo());
        Assertions.assertTrue(updatedMemberOptional.isPresent());
        Assertions.assertEquals("newPassword", updatedMemberOptional.get().getMbPassword());

    }

    @Test
    @DisplayName("회원조회-by-회원번호")
    void findByMbNo() {

        Member member1 = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.saveAndFlush(member1);

        Optional<Member> actualOptional = memberRepository.findByMbNo(member1.getMbNo());

        Assertions.assertTrue(actualOptional.isPresent());
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
        memberRepository.saveAndFlush(member1);

        Optional<Member> actualOptional = memberRepository.findByMbNo(member1.getMbNo());
        Assertions.assertTrue(actualOptional.isPresent());

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
        memberRepository.saveAndFlush(member1);

        Optional<Member> actualOptional = memberRepository.findByMbEmail("marco@nhnacademy.com");
        Assertions.assertTrue(actualOptional.isPresent());

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
    void existsByMbNo(){
        Member member= Member.ofNewMember("marco@nhnacademy.com",  "marco","password","01012345678");
        memberRepository.saveAndFlush(member);
        long memberNo = member.getMbNo();
        log.debug("member : {}", member);

        boolean actual = memberRepository.existsByMbNo(memberNo);
        Assertions.assertTrue(actual);
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
        memberRepository.saveAndFlush(member);
        long memberNo = member.getMbNo();

        Optional<Member> dbMemberOptional = memberRepository.findByMbNo(memberNo);
        dbMemberOptional.ifPresent(dbMember -> {
            dbMember.withdraw();
            memberRepository.saveAndFlush(dbMember);
        });

        boolean actual = memberRepository.existsByMbNoAndWithdrawalAtIsNotNull(memberNo);
        assertTrue(actual);
    }

    @Test
    @DisplayName("회원 탈퇴여부: false")
    void isMemberWithdrawn_false(){
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.saveAndFlush(member);
        long memberNo = member.getMbNo();

        boolean actual = memberRepository.existsByMbNoAndWithdrawalAtIsNotNull(memberNo);
        assertFalse(actual);
    }

    @Test
    @DisplayName("회원탈퇴:탈퇴일자 수정")
    void updateWithdrawalAt() {
        Member member= Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.saveAndFlush(member);
        long memberNo = member.getMbNo();
        log.debug("탈퇴전 : member:{}",member.getWithdrawalAt());

        Optional<Member> dbMemberOptional = memberRepository.findByMbNo(memberNo);
        dbMemberOptional.ifPresent(dbMember -> {
           dbMember.withdraw();
           memberRepository.saveAndFlush(dbMember);
        });

        Optional<Member>  withdrawMemberOptional = memberRepository.findByMbNo(memberNo);
        Assertions.assertTrue(withdrawMemberOptional.isPresent());
        log.debug("탈퇴 후 : member:{}",withdrawMemberOptional.get().getWithdrawalAt());
    }
}
