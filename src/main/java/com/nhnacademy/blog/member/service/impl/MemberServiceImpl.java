package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.common.exception.UnauthorizedException;
import com.nhnacademy.blog.common.security.PasswordEncoder;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.*;
import com.nhnacademy.blog.member.repository.JpaMemberRepository;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * TODO#4 Spring 기반의 Service 환경(구성)
 */
@Slf4j

/**
 * TODO#4-1 @Service
 * 이 어노테이션은 Spring의 서비스 계층(Component)로 사용되는 클래스에 적용됩니다.
 * 서비스 계층은 비즈니스 로직을 처리하는 역할을 하며, @Service 어노테이션을 사용하면 Spring이 해당 클래스를 자동으로
 * 빈(Bean)으로 등록하여 의존성 주입을 할 수 있게 됩니다.
 * 이 어노테이션은 주로 서비스 클래스에 사용되며, 해당 클래스가 비즈니스 로직을 처리하는 역할을 담당한다고
 * Spring에게 알려줍니다.
 *
 * @Service는 @Component의 특수화된 형태이기 때문에, Spring의 컴포넌트 스캔에 의해 자동으로 빈으로 등록됩니다.
 * 따라서 다른 컴포넌트나 클래스에서 의존성 주입을 통해 사용할 수 있습니다.
 */
@Service

/**
 * TODO#4-2 @Transactional
 * 이 어노테이션은 메소드 또는 클래스 수준에 적용되어 트랜잭션 처리를 자동으로 관리합니다.
 * 트랜잭션은 데이터베이스 작업에서 여러 연산을 하나의 단위로 묶어, 그 연산들이 모두 성공하거나 실패해야 하는 조건을 처리합니다.
 * @Transactional은 주로 서비스 계층에서 사용되며, 메소드 실행이 끝나면 트랜잭션을 자동으로 커밋하거나 롤백합니다.
 *
 * 이 어노테이션이 적용된 메소드가 실행되면 Spring은 트랜잭션을 시작하고, 메소드 실행 후 자동으로 트랜잭션을
 * 커밋합니다. 만약 예외가 발생하면, 트랜잭션을 롤백합니다. 롤백 조건은 기본적으로 `RuntimeException`과 `Error`에 대해서만 적용됩니다.
 * 예외를 다룰 때 `@Transactional(rollbackFor = Exception.class)`와 같이 설정하여 커스텀 예외를 지정할 수도 있습니다.
 * @Transactional
 */
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    /**
     * TODO#4-3 생성자 주입(Constructor Injection)
     * Spring에서 의존성 주입을 받는 방법 중 하나입니다.
     * 생성자 주입은 클래스의 생성자를 통해 필요한 의존 객체를 전달받습니다.
     * 이 방식은 다음과 같은 장점이 있습니다:
     * - 1. 불변성 보장: 생성자에서 초기화된 의존성은 변경할 수 없으므로,
     *      객체가 생성된 이후 의존성이 변경되는 것을 방지할 수 있습니다.
     * - 2. 테스트 용이성: 생성자 주입은 의존성 주입을 받는 클래스의 생성자가 명확하게 정의되므로,
     *      테스트할 때 어떤 의존성이 필요한지 쉽게 알 수 있습니다.
     * - 3. Null 체크 자동화: Spring이 의존성 주입을 담당하기 때문에, 생성자에서 의존 객체가 `null`이 될 일이 없으며,
     *      의존성이 누락되었을 경우 컴파일 시점에 오류가 발생하여 버그를 방지할 수 있습니다.
     *
     * 생성자 주입은 필드 주입이나 세터 주입에 비해 안정적인 의존성 주입 방법으로 권장됩니다.
     *
     * TODO#4-4 @Qualifier는 동일 타입의 빈이 여러 개 있을 때 특정 빈을 선택적으로 주입하는 데 사용됩니다.
     * 하지만 여기에서는 MemberRepository와 PasswordEncoder 두 객체는 서로 다른 타입이므로,
     * @Qualifier를 사용하지 않아도 Spring이 자동으로 해당 타입에 맞는 빈을 주입합니다.
     * 따라서 @Qualifier를 사용할 필요는 없습니다.
     * 만약 PasswordEncoder 구현체가 여러 개 존재하면, @Qualifier를 사용하여 특정 구현체를 명시적으로 주입해야만,
     * 의도한 구현체가 정확히 주입될 수 있습니다.
     * 즉, 구현체가 한 개 이상 존재할 때 명시적으로 BeanName을 지정해서 주입할 수 있습니다.
     *
     * @param memberRepository
     * @param passwordEncoder
     */
    private final JpaMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(JpaMemberRepository memberRepository,
                             PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * TODO#4-5 이전 생성자 , 4-4와 비교 합니다.
     */
//    public MemberServiceImpl(@Qualifier(JdbcMemberRepository.BEAN_NAME) MemberRepository memberRepository,
//                             @Qualifier(BCryptPasswordEncoder.BEAN_NAME) PasswordEncoder passwordEncoder
//    ) {
//        this.memberRepository = memberRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    @Transactional
    @Override
    public MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest) {

        //1.이메일 중복체크
        checkEmailDuplicate(memberRegisterRequest.getMbEmail());

        //2.모바일_연락처 중복체크
        checkMobileDuplicate(memberRegisterRequest.getMbMobile());

        //3. password를 Bcrypt 암호화, https://github.com/djmdjm/jBCrypt
        String mbPassword = passwordEncoder.encode(memberRegisterRequest.getMbPassword());

        Member member = Member.ofNewMember(
                memberRegisterRequest.getMbEmail(),
                memberRegisterRequest.getMbName(),
                mbPassword,
                memberRegisterRequest.getMbMobile()
        );
        memberRepository.save(member);

        return getMember(member.getMbNo());
    }

    @Transactional
    @Override
    public MemberResponse updateMember(MemberUpdateRequest memberUpdateRequest) {

        //1.회원 탈퇴여부 체크
        checkWithdrawal(memberUpdateRequest.getMbNo());

        //2.회원 존재여부 체크
        checkMemberExists(memberUpdateRequest.getMbNo());
        Optional<Member> memberOptional =  memberRepository.findByMbNo(memberUpdateRequest.getMbNo());
        Member member = memberOptional.get();

        //이메일 수정  체크
        if(!member.getMbEmail().equals(memberUpdateRequest.getMbEmail())) {
            //3.이메일 중복체크
            checkEmailDuplicate(memberUpdateRequest.getMbEmail());
        }

        //모바일 연락처 수정 체크
        if(!member.getMbMobile().equals(memberUpdateRequest.getMbMobile())) {
            //4.모바일_연락처 중복체크
            checkMobileDuplicate(memberUpdateRequest.getMbMobile());
        }

        //회원정보(수정), 변경감지
        member.update(
                memberUpdateRequest.getMbEmail(),
                memberUpdateRequest.getMbName(),
                memberUpdateRequest.getMbMobile()
        );


        return getMember(member.getMbNo());
    }

    @Transactional
    @Override
    public void withdrawalMember(long mbNo) {

        //회원존재여부 체크
        checkMemberExists(mbNo);

        //탈퇴한 회원인지 체크
        checkWithdrawal(mbNo);

        //탈퇴일자를 수정한다.
        //memberRepository.updateWithdrawalAt(mbNo, LocalDateTime.now());

    }

    @Override
    public MemberResponse getMember(long mbNo) {

        Optional<Member> memberOptional = memberRepository.findByMbNo(mbNo);

        if(memberOptional.isEmpty()){
            throw new NotFoundException("Member with Mb No : " + mbNo + " not found");
        }

        Member member = memberOptional.get();
        return new MemberResponse(
                member.getMbNo(),
                member.getMbEmail(),
                member.getMbName(),
                member.getMbMobile(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getWithdrawalAt()
        );
    }

    @Transactional
    @Override
    public void changePassword(MemberPasswordUpdateRequest memberPasswordUpdateRequest) {

        //1.회원존재여부 체크
        checkMemberExists(memberPasswordUpdateRequest.getMbNo());

        //2.oldPassword 일치하는지 검증
        Optional<Member> memberOptional = memberRepository.findByMbNo(memberPasswordUpdateRequest.getMbNo());
        Member member = memberOptional.get();

        log.debug("oldPassword:{}", memberPasswordUpdateRequest.getOldPassword());
        log.debug("mbPassword:{}", member.getMbPassword());

        if(!passwordEncoder.matches(memberPasswordUpdateRequest.getOldPassword(), member.getMbPassword())) {
            //3.oldPassword가 일치하지 않다면 예외처리
            throw new BadRequestException("password dose not match");
        }

        //newPassword Bcrypt 암호화
        String newPassword = passwordEncoder.encode(memberPasswordUpdateRequest.getNewPassword());
        member.changePassword(newPassword);
    }

    @Override
    public LoginMember doLogin(LoginRequest loginRequest) {

        Optional<Member> memberOptional = memberRepository.findByMbEmail(loginRequest.getEmail());
        //1.회원이 존재하지 않을 때
        if(memberOptional.isEmpty()){
            throw new UnauthorizedException("login failed");
        }

        //2.회원은 존재하지만 password가 일치하지 않을 때
        boolean isPasswordMatch = passwordEncoder.matches(loginRequest.getPassword(), memberOptional.get().getMbPassword());
        if(!isPasswordMatch){
            throw new UnauthorizedException("login failed");
        }

        return new LoginMember(
                memberOptional.get().getMbNo(),
                memberOptional.get().getMbEmail(),
                memberOptional.get().getMbName()
        );
    }

    protected void checkMemberExists(long mbNo) {
        boolean flag = memberRepository.existsByMbNo(mbNo);
        if(!flag) {
           throw new NotFoundException("Member not found with mb no: %s ".formatted(mbNo));
        }
    }

    protected void checkEmailDuplicate(String email) {
        boolean flag = memberRepository.existsByMbEmail(email);
        if(flag) {
            throw new ConflictException("Email [%s] already exists".formatted(email));
        }
    }

    protected void checkMobileDuplicate(String mobile) {
        boolean flag = memberRepository.existsByMbMobile(mobile);
        if(flag) {
            throw new ConflictException("Mobile [%s] already exists".formatted(mobile));
        }
    }

    protected void checkWithdrawal(Long mbNo) {
        boolean flag = memberRepository.existsByMbNoAndWithdrawalAtIsNotNull(mbNo);
        if(flag) {
            throw new NotFoundException("This member [%d] has bean withdrawal".formatted(mbNo));
        }
    }
}
