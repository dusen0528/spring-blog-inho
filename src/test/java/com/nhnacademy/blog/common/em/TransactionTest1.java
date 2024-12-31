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

import java.util.Objects;

/**
 *
 * - 직접 EntityManagerFactory로 부터 EntityManager를 생성하고 Member Entity를 기반으로 Transaction 처리를 합니다.
 */

@Slf4j
// @ActiveProfiles을 사용해 test profile 환경을 설정합니다.
@ActiveProfiles("test")
// @ExtendWith()을 사용해 Spring 기반의 test 환경을 구성합니다.
@ExtendWith({SpringExtension.class})
// @ContextConfiguration()을 사용하여 ApplicationConfig.class 기반으로 Context환경을 구성 합니다.
@ContextConfiguration(classes = ApplicationConfig.class)
public class TransactionTest1 {

    //EntityManagerFactory 주입 합니다.
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    //PlatformTransactionManager 주입 합니다.
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("EntityManager를 이용한 Transaction처리")
    void transactionTestUsingEntityManager() {

        Member member;
        EntityTransaction entityTransaction = null;
        //entityManagerFactory.createEntityManager() 호출해서  entityManager를 생성 합니다.
        try(EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            //entityManager.getTransaction()을 호출해서 transaction을 획득 합니다.
            entityTransaction = entityManager.getTransaction();
            //`entityTransaction.begin()`를 호출해서 트랜잭션 시작 합니다.
            entityTransaction.begin();

            //member entity를 생성 합니다.
            member = Member.ofNewMember(
                    "marco@nhnacademy.com",
                    "마르코",
                    "12345",
                    "01012345678"
            );

            //entityManager의  persist() 호출해서 엔티티를 영속화 합니다.
            entityManager.persist(member);

            //entityTransaction를 이용해서 commit() 합니다.
            entityTransaction.commit();

            log.debug("Member: {}", member);

        } catch (Exception e) {
            //예외 발생 시  entityTransaction을 이용해서 트랜잭션 롤백 합니다.
            if(Objects.nonNull(entityTransaction)){
                entityTransaction.rollback();
            }
            throw e;
        }
        //member에 mbNo가 NOT NULL인지 검증(Assertions) 합니다.
        Assertions.assertNotNull(member.getMbNo());
    }

}
