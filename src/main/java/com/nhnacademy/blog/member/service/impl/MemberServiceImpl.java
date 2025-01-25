package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.security.PasswordEncoder;
import com.nhnacademy.blog.common.security.exception.ConflictException;
import com.nhnacademy.blog.common.security.exception.NotFoundException;
import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public MemberResponse registerMember(MemberRegisterRequest memberRegisterRequest) {

        //1.이메일 중복체크
        boolean isExistsEmail = memberRepository.existsByMbEmail(memberRegisterRequest.getMbEmail());
        if(isExistsEmail) {
            throw new ConflictException("Member email [%s] already exists".formatted(memberRegisterRequest.getMbEmail()));
        }

        //2. 모바일_연락처 중복체크
        boolean isExistMobile = memberRepository.existsByMbMobile(memberRegisterRequest.getMbMobile());
        if(isExistMobile) {
            throw new ConflictException("Member mobile [%s] already exists".formatted(memberRegisterRequest.getMbMobile()));
        }

        String mbPassword = passwordEncoder.encode(memberRegisterRequest.getMbPassword());
        Member member = Member.ofNewMember(memberRegisterRequest.getMbEmail(), memberRegisterRequest.getMbName(), mbPassword, memberRegisterRequest.getMbMobile());
        memberRepository.save(member);

        Optional<Member> memberOptional = memberRepository.findById(member.getMbNo());
        if(memberOptional.isPresent()) {
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

        throw new NotFoundException("Member not found");
    }

    @Override
    public LoginMember doLogin(String mbEmail, String mbPassword) {
        Optional<Member> memberOptional = memberRepository.findByMbEmail(mbEmail);

        if(memberOptional.isPresent()) {
            Member member = memberOptional.get();
            if(passwordEncoder.matches(mbPassword, member.getMbPassword())){
                return LoginMember.of(
                        member.getMbNo(),
                        member.getMbEmail(),
                        member.getMbName()
                );
            }
        }

        throw new UnauthorizedException("Unauthorized");
    }

    @Override
    public MemberResponse getMember(long mbNo) {
        Optional<Member> memberOptional = memberRepository.findById(mbNo);
        if(memberOptional.isPresent()) {
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
        
        throw new NotFoundException("Member not found");
    }

    @Override
    public MemberResponse getMemberByEmail(String mbEmail) {
        Optional<Member> memberOptional = memberRepository.findByMbEmail(mbEmail);
        if(memberOptional.isPresent()) {
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

        throw new NotFoundException("Member not found");
    }
}
