package com.nhnacademy.blog.member.domain;

import com.nhnacademy.blog.common.config.ApplicationConfig;
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

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class MemberTest {
    /**
     * EntityManager를  field 주입 합니다.
     */
    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("1차 캐시에서 member조회")
    void findFromCache(){
        //member entity 생성
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );

        //member Entity -> 1차 캐시에서 저장, 영속화 합니다.
        entityManager.persist(member);

        //clear() 메서드를 호출해서 entityManager를 초기화 합니다.
        entityManager.clear();

        /**
         *1차 캐시에서 Member 조회
         *  - 만약 entityManager.find()호출시 entity가 1차 캐시에 없으면 EntityManager는 데이터베이스에서 조회해서 Entity를 생성 합니다.
         *  - findMember-start ~ findMember-end 사이에 select query가 발생 합니다.
         */
        log.debug("findMember-start-----------------------------");
        Member cachedMember = entityManager.find(Member.class, member.getMbNo());
        log.debug("cachedMember: {}", cachedMember);
        log.debug("findMember-end-------------------------------");

        /**
         *entityManager의 clear() 호출 합니다.
         * = 모든 엔티티 관리 해제: 현재 영속성 컨텍스트에 포함된 모든 엔티티 객체를 관리 해제(detach)합니다.
         * - 관리 해제된 엔티티는 더 이상 영속성 컨텍스트에 의해 관리되지 않으며, 상태 변경이 데이터베이스에 반영되지 않습니다.
         * - 영속성 컨텍스트 초기화: 영속성 컨텍스트를 초기 상태로 되돌립니다. 즉, 새로 생성된 것처럼 비워집니다.
         * - 영속성 컨텍스르틑 초기화 했음으로 1차 캐시에 Member Entity가 존재하지 않습니다.
         * - 즉 select query가 발생 합니다.
         */
        entityManager.clear();

        /**
         * member Entity 조회
         * entity manager가 clear된 상태에서 다시 조회하면 member entity가 영속성 컨텍스트에 존재하지 않음으로 데이테베이스에서 조회 후
         * 1차 캐시에 캐싱 합니다.
         * select query가 발생 합니다. (로그를 확인하세요)
         */
        Member member2 = entityManager.find(Member.class, member.getMbNo());
        log.debug("after cler(): {}", cachedMember);

        /**
         * member2 entity를 검증하세요
         */
        Assertions.assertNotNull(member2);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(member.getMbNo(), member2.getMbNo());
                    Assertions.assertEquals("마르코", member2.getMbName());
                    Assertions.assertEquals("marco@nhnacademy.com", member2.getMbEmail());
                    Assertions.assertEquals("12345", member2.getMbPassword());
                }
        );
    }

    @Test
    @DisplayName("Entity 수정-변경감지 + 지연쓰기")
    void updateTest(){

        /**
         * 비영속 상태의 Member Entity를 생성 합니다.
         */
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );

        // member entity를 1차 캐시에서 저장, 즉 영속화 상태로 변경 합니다.
        entityManager.persist(member);

        /* member 수정
        *   - email :  test@nhnacademy.com,
        *   - mbName  : 테스트,
        *   - mbMobile : 01011112222
        * */
        member.update("test@nhnacademy.com","테스트","01011112222");

        // - 변경 내용 적용: 현재 영속성 컨텍스트에 있는 엔티티의 변경 사항을 데이터베이스에 반영합니다.
        // - 즉, 영속성 컨텍스트에서 관리되는 모든 엔티티의 상태 변화가 데이터베이스에 기록됩니다.
        // - 엔티티 상태 동기화: flush()가 호출되면 영속성 컨텍스트의 모든 엔티티가 데이터베이스의 상태와 동기화됩니다.
        // - 쿼리 실행 전 보장: 데이터베이스 쿼리를 실행하기 전에 영속성 컨텍스트의 변경 사항이 적용되도록 보장합니다.
        // - 따라서, JPQL이나 네이티브 쿼리를 실행하기 전에 flush()를 호출하여 일관된 상태를 유지할 수 있습니다.
        // - update query가 발생 합니다. @Rollback(false) 설정으로 직접 flush() 호출할 필요 없습니다.
        // entityManager.flush();

        // 비밀번호를 'changePassword' 로 변경 합니다.
        member.changePassword("changePassword");

        /**
         * 변경감지
         * - entityManager.persist(member) : 직접 호출하지 않더라도 entity가 변경이 되었다면 이를 감지하고 필요할 때 데이터베이스에 변경 사항을 commit() 호출되면 데이터베이스 반영합니다.
         */

        /**
         * 쓰기지연
         * = method update() + member.chagePassword() 변경사항을 1차케시에 반영하여 하나의 UPDATE문이 발생 합니다.
         * - QUERY LOG를 직접 확인하세요.
         *
         * update members
         * set
         *      mb_email='test@nhnacademy.com',
         *      mb_mobile='01011112222',
         *      mb_name='테스트',
         *      mb_password='changePassword',
         *      updated_at='2024-12-31T11:59:18.351+0900',
         *      withdrawal_at=NULL
         *  where
         *      mb_no=1;
         */

        log.debug("member: {}", member);

        /**
         * member entity를 조회 합니다.
         */
        Member member2 = entityManager.find(Member.class, member.getMbNo());

        /**
         * member2 entity를 검증하세요
         */
        Assertions.assertNotNull(member2);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(member.getMbNo(), member2.getMbNo());
                    Assertions.assertEquals("테스트", member2.getMbName());
                    Assertions.assertEquals("test@nhnacademy.com", member2.getMbEmail());
                    Assertions.assertEquals("changePassword", member2.getMbPassword());
                }
        );
    }

    @Test
    @DisplayName("삭제 테스트")
    void deleteTest(){
        // 회원 entity생성, 비영속 상태
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );

        //member entity 영속화,즉 영속 상태
        entityManager.persist(member);

        //entity를 삭제하기 위해서는 삭제 대상 entity를 조회해야 합니다.
        Member deletTarget = entityManager.find(Member.class, member.getMbNo());

        /**
         * remove()호출해서 entity를 삭제 합니다.<-- 삭제상태
         * - remove()호출해서 삭제된 entity는 재사용 하지말고 자연습럽게 가비지 컬렉션의 대상이 되도록 가만히 두는게 좋습니다.
         * - 즉, 삭제한 entity는 재사용하지 마세요.
         */
        entityManager.remove(deletTarget);

    }

    @Test
    @DisplayName("준 영속성-테스트")
    void detachTest(){
        // 회원 entity생성, 비영속 상태
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );

        // 영속화 상태 변경
        entityManager.persist(member);

        //entityManager의 detach() 메서드를 호출하여 준영속 상태로 변경
        entityManager.detach(member);
        /**
         * member.update() 메서드를 호출해서 회원 정보를 수정 합니다.
         * 준영속 상태 임으로, member entity의 update() 메서드를 호출해도 아무일도 일어나지 않습니다.
         * - 즉 더이상 영속성 컨텍스트에게 Member Entity를 관리하지 마라는 의미 입니다.
         * - 변경감지가 일어나지 않습니다.
         */
        member.update("test@nhnacademy.com","테스트","01011112222");

        // member entity를 조회 합니다.
        Member member2 = entityManager.find(Member.class, member.getMbNo());
        log.debug("member: {}", member2);

        /**
         *
         *  - member2 entity를 검증하는 테스트 코드를 작성합니다.
         */
        Assertions.assertNotNull(member2);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals("marco@nhnacademy.com", member2.getMbEmail());
                },
                ()->{
                    Assertions.assertEquals("마르코", member2.getMbName());
                },
                ()->{
                    Assertions.assertEquals("12345", member2.getMbPassword());
                },
                ()->{
                    Assertions.assertEquals("01012345678", member2.getMbMobile());
                }
        );
    }

    @Test
    @DisplayName("병합 테스트 - 준영속성 상태의 entity를 영속성 상태의 entity로 변경")
    void margeTest(){
        // 회원 entity생성, 비영속 상태
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );

        // member entity 영속화
        entityManager.persist(member);

        // entityManager의 detach() 메서드를 후출해서 member entity를 준영속 상태로 변경 합니다.
        entityManager.detach(member);

        /**
         * member entity 수정 합니다.
         * - mbEmail : test@nhnacademy.com
         * - mbName : 테스트
         * - mbMobile : 01011112222
         */
        member.update("test@nhnacademy.com","테스트","01011112222");

        /**
         * - 준영속 상태의 entity ->  영속 상태의 entity로 변경 합니다. (즉 영속성 컨텍스에 의해서 관리 됩니다.)
         * - merge() -병합은 비영속 상태의 entity도 영속상태의 entity로 변경할 수 있습니다.
         * - merge()를 실행하면 파라미터로 전달 받은 준영속(or 비영속) entity의 식별자(@Id)값으로 1차 캐시에서 entity를 조해 합니다.
         * - 만약 1차 캐시에 entity가 존재하지 않다면 데이터베이스(MYSQL OR H2) 에서  entity를 조회하고 1차 캐시에 캐싱 합니다.
         * - 즉 병합은 준영속, 비영속 entity를 신경쓰지 않습니다. save or update기능을 수행 합니다.
         */

        entityManager.merge(member);

        /**
         * member를 조회 합니다.
         * - member entity가 MERGE되었음으로 영속성 컨텍스트에 의해서 관리 됩니다.
         * - mergedMember-start() ~ mergedMember-end() 데이터를 조회해도 1차 캐시에 의해 select 문이 발생하지 않습니다. (로그를 확인하세요)
         */
        log.debug("mergedMember-start()------------------------");
        Member member2 = entityManager.find(Member.class, member.getMbNo());
        log.debug("mergedMember-end()------------------------");

        /**
         * member2를 검증하는  테스트 코드를 작성합니다.
         */
        Assertions.assertNotNull(member2);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals("test@nhnacademy.com", member2.getMbEmail());
                },
                ()->{
                    Assertions.assertEquals("테스트", member2.getMbName());
                },
                ()->{
                    Assertions.assertEquals("12345", member2.getMbPassword());
                },
                ()->{
                    Assertions.assertEquals("01011112222", member2.getMbMobile());
                }
        );
    }
}