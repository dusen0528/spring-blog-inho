package com.nhnacademy.blog.manager.account.impl;

import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.manager.account.dto.BlogAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * 통합 테스트 진행
 */
@Slf4j
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ApplicationConfig.class)
class DefaultBlogAccountManagerTest {
    //통합 테스트 진행

    @Autowired
    DefaultBlogAccountManager accountManager;

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