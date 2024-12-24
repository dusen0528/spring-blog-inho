package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.common.exception.UnauthorizedException;
import com.nhnacademy.blog.common.security.PasswordEncoder;
import com.nhnacademy.blog.common.security.impl.BCryptPasswordEncoder;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.*;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.repository.impl.JdbcMemberRepository;
import com.nhnacademy.blog.member.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository,
                             PasswordEncoder passwordEncoder
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

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

        //회원정보(수정)
        memberRepository.update(memberUpdateRequest);

        return getMember(member.getMbNo());
    }

    @Override
    public void withdrawalMember(long mbNo) {

        //회원존재여부 체크
        checkMemberExists(mbNo);

        //탈퇴한 회원인지 체크
        checkWithdrawal(mbNo);

        //탈퇴일자를 수정한다.
        memberRepository.updateWithdrawalAt(mbNo, LocalDateTime.now());
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

        memberRepository.updatePassword(memberPasswordUpdateRequest.getMbNo(), newPassword);
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

    private void checkMemberExists(long mbNo) {
        boolean flag = memberRepository.existsByMbNo(mbNo);
        if(!flag) {
           throw new NotFoundException("Member not found with mb no: %s ".formatted(mbNo));
        }
    }

    private void checkEmailDuplicate(String email) {
        boolean flag = memberRepository.existsByMbEmail(email);
        if(flag) {
            throw new ConflictException("Email [%s] already exists".formatted(email));
        }
    }

    private void checkMobileDuplicate(String mobile) {
        boolean flag = memberRepository.existsByMbMobile(mobile);
        if(flag) {
            throw new ConflictException("Mobile [%s] already exists".formatted(mobile));
        }
    }

    private void checkWithdrawal(Long mbNo) {
        boolean flag = memberRepository.isMemberWithdrawn(mbNo);
        if(flag) {
            throw new NotFoundException("This member [%d] has bean withdrawal".formatted(mbNo));
        }
    }

}
