package com.nhnacademy.blog.blogmember.domain;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class BlogMemberMappingTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("BlogMemberMapping 등록")
    void saveTest(){
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );
        entityManager.persist(member);



    }

    void deleteTest(){

    }
}