package com.nhnacademy.blog.member.repository;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        //TODO#11 - 테스트 메서드가 실행 될 때 마다 맴버를 10명 미리 등록 합니다.
        for (int i=0; i<10; i++) {
            String mbEmail = "marco-%s@nhnacademy.com".formatted(i);
            String mbName = "마르코-%s".formatted(i);
            String mbPassword = "12345";
            String mbMobile="0101111000%d".formatted(i);
            Member member = Member.ofNewMember(mbEmail, mbName, mbPassword, mbMobile);
            if(i%2==0){
                member.withdraw();
            }
            memberRepository.save(member);
        }
    }

    @Test
    @DisplayName("email로 회원조회")
    void findByMbEmail() {
        /**
         * TODO#12 -email로 회원조회
         */
        Optional<Member> memberOptional = memberRepository.findByMbEmail("marco-1@nhnacademy.com");
        Assertions.assertTrue(memberOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals("marco-1@nhnacademy.com", memberOptional.get().getMbEmail()),
                ()->Assertions.assertEquals("마르코-1", memberOptional.get().getMbName()),
                ()->Assertions.assertEquals("12345",memberOptional.get().getMbPassword()),
                ()->Assertions.assertEquals("01011110001",memberOptional.get().getMbMobile())
        );
    }

    @Test
    @DisplayName("전화번호로 회원조회")
    void findByMbMobile() {
        /**
         * TODO#13 - 전화번호로 회원조회 구현
         */
        Optional<Member> memberOptional = memberRepository.findByMbMobile("01011110001");
        Assertions.assertTrue(memberOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals("marco-1@nhnacademy.com", memberOptional.get().getMbEmail()),
                ()->Assertions.assertEquals("마르코-1", memberOptional.get().getMbName()),
                ()->Assertions.assertEquals("12345",memberOptional.get().getMbPassword()),
                ()->Assertions.assertEquals("01011110001",memberOptional.get().getMbMobile())
        );
    }

    @Test
    @DisplayName("email 존재여부 체크")
    void existsByMbEmail() {
        /**
         * TODO#14 - email 존재여부 체크
         */
        boolean actual = memberRepository.existsByMbEmail("marco-1@nhnacademy.com");
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("전화번호 존재여부 체크")
    void existsByMbMobile() {
        /**
         * TODO#15 - 전화번호 존재여부 체크 구현
         */
        boolean actual = memberRepository.existsByMbMobile("01011110001");
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("이용중이 회원 리스트 - 가입일기준 오름차순 정렬")
    void findByWithdrawalAtIsNotNullOrderByCreatedAtAsc() {
        /**
         * TODO#16 - 이용중이 회원 리스트 - 가입일기준 오름차순 정렬
         */
        List<Member> members = memberRepository.findByWithdrawalAtIsNotNullOrderByCreatedAtAsc();
        for (Member member : members) {
            log.debug("member:{}",member);
        }
        Assertions.assertEquals(5, members.size());
    }

    @Test
    @DisplayName("탈퇴한 회원 리스트 - 가입일 기준 내림차순 정렬")
    void findByWithdrawalAtIsNullOrderByCreatedAtDesc() {
        /**
         * TODO#17 - 탈퇴한 회원 리스트 - 가입일 기준 내림차순 정렬
         */
        List<Member> members = memberRepository.findByWithdrawalAtIsNullOrderByCreatedAtDesc();
        for (Member member : members) {
            log.debug("member:{}",member);
        }
        Assertions.assertEquals(5, members.size());
    }

    @Test
    @DisplayName("이용중인 회원 카운트")
    void countByWithdrawalAtIsNotNull() {
        /**
         * TODO#18 - 이용중인 회원 카운트
         */
        long actual = memberRepository.countByWithdrawalAtIsNotNull();
        Assertions.assertEquals(5, actual);
    }

    @Test
    @DisplayName("탈퇴한 회원 카운트")
    void countByWithdrawalAtIsNull() {
        /**
         * TODO#19 - 탈퇴한 회원 카운트
         */
        long actual = memberRepository.countByWithdrawalAtIsNull();
        Assertions.assertEquals(5, actual);
    }

    @Test
    @DisplayName("Entity가 아닌 MemberResponse 응답")
    void findAllByOrderByCreatedAtAsc() {

        /**
         * TODO#20 - Entity가 아닌 MemberResponse 응답
         */

        List<MemberResponse> memberResponseList = memberRepository.findAllByOrderByCreatedAtAsc();
        for(MemberResponse memberResponse : memberResponseList){
            log.debug("---------------------------------------------");
            log.debug("mbEmail:{}",memberResponse.getMbEmail());
            log.debug("mbName:{}",memberResponse.getMbName());
            log.debug("mbMobile:{}",memberResponse.getMbMobile());
            log.debug("createdAt:{}",memberResponse.getCreatedAt());
            log.debug("updatedAt:{}",memberResponse.getUpdatedAt());
            log.debug("withdrawalAt:{}",memberResponse.getWithdrawalAt());
        }
        Assertions.assertEquals(10, memberResponseList.size());
    }
}