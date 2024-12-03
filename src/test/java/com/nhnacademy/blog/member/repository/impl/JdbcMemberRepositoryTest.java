package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.config.impl.InitProcessor;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.config.impl.RepositoryProcessor;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcMemberRepositoryTest {
    static MemberRepository memberRepository;

    @BeforeAll
    static void beforeAll() {

        //connection pool(dbcp2) 생성 및 초기화
        InitProcessor initProcessor = new InitProcessor();
        initProcessor.process();

        //repository 생성
        RepositoryProcessor repositoryProcessor = new RepositoryProcessor();
        repositoryProcessor.process();

        Context context = ContextHolder.getApplicationContext();
        memberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
        DbConnectionThreadLocal.initialize();


    }

    @AfterAll
    static void tearDownAll() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Order(1)
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

    @Order(2)
    @Test
    void update() {

    }

    @Test
    void delete() {

    }

    @Test
    void changePassword(){

    }

    @Test
    void findByMbNo() {
    }

    @Test
    void findByMbEmail() {

    }

    @Test
    void findByMbMobile() {

    }

    @Test
    void existsByMbNo() {
        boolean actual = memberRepository.existsByMbNo(1l);

        assertFalse(actual);
    }

    @Test
    void existsByMbEmail() {
    }

    @Test
    void existsByMbMobile() {
    }

    @Test
    void updateWithdrawalAt() {
    }
}