package com.nhnacademy.blog.member.service.impl;

import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.dto.MemberUpdateRequest;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.repository.impl.JdbcMemberRepository;
import com.nhnacademy.blog.member.service.MemberService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service(name = MemberServiceImpl.BEAN_NAME )
public class MemberServiceImpl implements MemberService {
    public static final String BEAN_NAME="memberService";

    private final MemberRepository memberRepository;

    public MemberServiceImpl(@Qualifier(JdbcMemberRepository.BEAN_NAME) MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public void registerMember(MemberRegisterRequest memberRegisterRequest) {

        //1.이메일 중복체크
        boolean duplicatedEmail = memberRepository.existsByMbEmail(memberRegisterRequest.getMbEmail());

        if(duplicatedEmail) {
            //예외처리
        }

        //2.모바일_연락처 중복체크
        boolean duplicatedMobile = memberRepository.existsByMbMobile(memberRegisterRequest.getMbMobile());
        if(duplicatedMobile) {
            //예외처리
        }

        Member member = Member.ofNewMember(
                memberRegisterRequest.getMbEmail(),
                memberRegisterRequest.getMbName(),
                memberRegisterRequest.getMbPassword(),
                memberRegisterRequest.getMbMobile()
        );

        memberRepository.save(member);
    }

    @Override
    public void updateMember(MemberUpdateRequest memberUpdateRequest) {
        //1. 회원존재여부체크
        boolean existMember = memberRepository.existsByMbNo(memberUpdateRequest.getMbNo());

        if(!existMember) {
            //회원이 존재하지 않다면 예외처리
        }

        //2. 이메일 중복체크

        //3. 모바일_연락처 중복체크

        //회원정보(수정)
        memberRepository.update(memberUpdateRequest);
    }

    @Override
    public void withdrawalMember(long mbNo) {
        boolean existMember = memberRepository.existsByMbNo(mbNo);
        //회원이 존재하지 않다면  예외처리
        if(!existMember) {

        }

        memberRepository.updateWithdrawalAt(mbNo, LocalDateTime.now());
    }

    @Override
    public MemberResponse getMember(long mbNo) {
        Optional<Member> memberOptional = memberRepository.findByMbNo(mbNo);

        if(memberOptional.isEmpty()) {
            //예외처리
        }

        Member member = memberOptional.get();

        MemberResponse memberResponse = new MemberResponse(
                member.getMbNo(),
                member.getMbEmail(),
                member.getMbName(),
                member.getMbMobile(),
                member.getCreatedAt(),
                member.getUpdatedAt(),
                member.getWithdrawalAt()
        );

        return memberResponse;
    }

}
