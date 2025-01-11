package com.nhnacademy.blog.member.repository;

import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

//@ActiveProfiles("test")
//@ExtendWith({SpringExtension.class})
//@ContextConfiguration(classes = ApplicationConfig.class)
//@Transactional

/**
 * TODO#19 - 더이상 jpa 관련된 테스트할 때 복잡하게 위 처럼 annotation들을 선언할 필요 없습니다.
 * @DataJpaTest , profile 변경이 필요하다면 @ActiveProfiles("test") 사용하면 됩니다.
 *  - @DataJpaTest은 jpa와 관련된 Bean들만 생성해서 테스트할 수 있는 환경을 구성합니다.
 *  - @Transactional도 포함되어 있습니다.
 *  - 참고로 테스트시 @Transactional을 사용하면 테스트 메서드 실행 후 항상 rollback이 됩니다.
 *  - 테스트 환경에서 설제 mysql 데이터베이스를 기준으로 테스트를 수행하려면 아래와 같이 사용하세요.
 *  - 테스트 데이터베이스(h2)를 사용하지 않습니다.
 *  @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
 *  @ActiveProfiles("test")
 *  @DataJpaTest
 */

@Slf4j
//테스트시 자동 데이터베이스 설정을 비활성화하겠다는 의미, 실제 mysql연동해서 테스트 할 떄 설정
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        //테스트 메서드가 실행 될 때 마다 맴버를 10명 미리 등록 합니다.
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
        boolean actual = memberRepository.existsByMbEmail("marco-1@nhnacademy.com");
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("전화번호 존재여부 체크")
    void existsByMbMobile() {
        boolean actual = memberRepository.existsByMbMobile("01011110001");
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("이용중이 회원 리스트 - 가입일기준 오름차순 정렬")
    void findByWithdrawalAtIsNotNullOrderByCreatedAtAsc() {
        List<Member> members = memberRepository.findByWithdrawalAtIsNotNullOrderByCreatedAtAsc();
        for (Member member : members) {
            log.debug("member:{}",member);
        }
        Assertions.assertEquals(5, members.size());
    }

    @Test
    @DisplayName("탈퇴한 회원 리스트 - 가입일 기준 내림차순 정렬")
    void findByWithdrawalAtIsNullOrderByCreatedAtDesc() {
        List<Member> members = memberRepository.findByWithdrawalAtIsNullOrderByCreatedAtDesc();
        for (Member member : members) {
            log.debug("member:{}",member);
        }
        Assertions.assertEquals(5, members.size());
    }

    @Test
    @DisplayName("이용중인 회원 카운트")
    void countByWithdrawalAtIsNotNull() {
        long actual = memberRepository.countByWithdrawalAtIsNotNull();
        Assertions.assertEquals(5, actual);
    }

    @Test
    @DisplayName("탈퇴한 회원 카운트")
    void countByWithdrawalAtIsNull() {
        //long actual = memberRepository.countByWithdrawalAtIsNull();
        //Assertions.assertEquals(5, actual);
    }

    @Test
    @DisplayName("Entity가 아닌 MemberResponse 응답")
    void findAllByOrderByCreatedAtAsc() {
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