package com.nhnacademy.blog.common.em;


import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@Slf4j

//@ActiveProfiles을 사용해 test profile 환경을 설정합니다.
@ActiveProfiles("test")

//@ExtendWith()을 사용해 Spring 기반의 test 환경을 구성합니다.
@ExtendWith({SpringExtension.class})

//@ContextConfiguration()을 사용하여 ApplicationConfig.class 기반으로 Context환경을 구성 합니다.
@ContextConfiguration(classes = ApplicationConfig.class)

//@Transactional annotation을 사용하여 스프링에 의해서 트렌젝션처리를 관리 받을 수 있도록 설정 합니다.
@Transactional
public class TransactionTest3 {

    //EntityManager를 주입 받습니다.
    @Autowired
    EntityManager entityManager;

    @DisplayName("EntityManager와 PlatformTransactionManager를 활용한 트랜잭션 처리")
    @Test
    void transactionTestUsingTransactional(){

        //member entity 생성
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );

        //member entity 영속화
        entityManager.persist(member);

        log.debug("member: {}", member);

        //mbNo, createdAt not null 여부를 검증(Assertions) 합니다.
        Assertions.assertNotNull(member.getMbNo());
        Assertions.assertNotNull(member.getCreatedAt());
    }

}
