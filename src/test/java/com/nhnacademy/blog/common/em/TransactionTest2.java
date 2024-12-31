package com.nhnacademy.blog.common.em;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Objects;

/**
 *
 * - 직접 EntityManagerFactory로 부터 EntityManager를 생성하고
 * - PlatformTransactionManager를 사용해서 Transaction 처리 합니다.
 */

@Slf4j
//@ActiveProfiles을 사용해 test profile 환경을 설정합니다.
@ActiveProfiles("test")
//@ExtendWith()을 사용해 Spring 기반의 test 환경을 구성합니다.
@ExtendWith({SpringExtension.class})
//@ContextConfiguration()을 사용하여 ApplicationConfig.class 기반으로 Context환경을 구성 합니다.
@ContextConfiguration(classes = ApplicationConfig.class)
public class TransactionTest2 {

    //EntityManagerFactory 주입 합니다.
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    //PlatformTransactionManager 주입 합니다.
    @Autowired
    private PlatformTransactionManager transactionManager;

    @DisplayName("EntityManager와 PlatformTransactionManager를 활용한 트랜잭션 처리")
    @Test
    void transactionTestUsingPlatformTransactionManager() {

        //트랜잭션 정의 객체 생성 합니다.
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();

        //ISOLATION LEVEL을 TransactionDefinition.ISOLATION_READ_COMMITTED 설정 합니다.
        defaultTransactionDefinition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        //PROPAGATION을 TransactionDefinition.PROPAGATION_REQUIRES_NEW 설정 합니다.
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        //transaction name을 'member ransaction'으로 설정 합니다.
        defaultTransactionDefinition.setName("member transaction");

        //transactionManager.getTransaction() 호출해서 트랜잭션을 시작 합니다.
        TransactionStatus transactionStatus = transactionManager.getTransaction(defaultTransactionDefinition);

        Member member;
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {
            //entityManager가 joinTransaction() METHOD를 호출해서 transactionStatus transaction에 참여 합니다.
            entityManager.joinTransaction();

            //Member entity 생성하세요
            member = Member.ofNewMember(
                    "marco@nhnacademy.com",
                    "마르코",
                    "12345",
                    "01012345678"
            );

            //member entity 영속화 합니다.
            entityManager.persist(member);

            //트랜잭션 매니저 커밋
            transactionManager.commit(transactionStatus);
            log.debug("Member: {}", member);
        } catch (Exception e) {
            //예외 발생 시  트랜잭션 매니저 롤백
            transactionManager.rollback(transactionStatus);
            throw e;
        }

        //member에 mbNo가 NOT NULL인지 검증(Assertions) 합니다.
        Assertions.assertNotNull(member.getMbNo());
    }
}
