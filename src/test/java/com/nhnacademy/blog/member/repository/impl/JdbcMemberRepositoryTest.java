package com.nhnacademy.blog.member.repository.impl;

import com.nhnacademy.blog.config.init.InitProcessor;
import com.nhnacademy.blog.config.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JdbcMemberRepositoryTest {
    MemberRepository memberRepository;

    @BeforeAll
    static void beforeAll() {
        //connection pool 생성 및 초기화
        InitProcessor initProcessor = new InitProcessor();
        initProcessor.process();
    }

    @BeforeEach
    void setUp() {
        memberRepository = new JdbcMemberRepository();
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
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