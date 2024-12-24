package com.nhnacademy.blog.manager.account.impl;

import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.manager.account.dto.BlogAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;

/**
 * 통합 테스트 진행
 */
@Slf4j
class DefaultBlogAccountManagerTest {
    //통합 테스트 진행

    static ApplicationContext context;
    static DefaultBlogAccountManager accountManager;

    @BeforeAll
    static void setUpAll() {
        context = ContextHolder.getApplicationContext();
        accountManager = (DefaultBlogAccountManager) context.getBean("defaultBlogAccountManager");
    }

    @BeforeEach
    void setUp() {
        DbConnectionThreadLocal.initialize();
    }
    @AfterEach
    void tearDown() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @AfterAll
    static void tearDownAll() {

    }

    @Test
    @DisplayName("blog 회원가입 - 통합테스트")
    void createAccount(){

        BlogAccountRequest blogAccountRequest = new BlogAccountRequest("marco@nhnacademy.com","마르코","12345","01082995258","marco");
        BlogResponse blogResponse = accountManager.createAccount(blogAccountRequest);
        log.debug("blogResponse:{}", blogResponse);

        Assertions.assertAll(
                ()->Assertions.assertNotNull(blogResponse.getBlogId()),
                ()->Assertions.assertNotNull(blogResponse.getCreatedAt()),
                ()->Assertions.assertNull(blogResponse.getUpdatedAt()),

                ()->Assertions.assertEquals("marco",blogResponse.getBlogFid()),
                ()->Assertions.assertEquals("changeMe", blogResponse.getBlogMbNickname()),
                ()->Assertions.assertEquals("welcome to my blog!", blogResponse.getBlogDescription()),

                ()->Assertions.assertTrue(blogResponse.getBlogIsPublic()),
                ()->Assertions.assertTrue(blogResponse.isBlogMain())
        );

    }

}