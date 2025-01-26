package com.nhnacademy.blog.common.config.security.userdetail;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * TODO#7 - UserDetailsService는 사용자의 인증 정보를 로드하는 데 필요한 인터페이스입니다.
 *  주로 사용자 이름을 기반으로 사용자의 인증 정보를 데이터베이스에서 조회하여,
 *  Spring Security의 인증 및 권한 처리 과정에서 사용됩니다.
 *  이를 구현함으로써, 인증 절차를 커스터마이즈하고, 사용자 정보 및 권한을 효율적으로 관리할 수 있습니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final BlogMemberMappingRepository blogMemberMappingRepository;

    /**
     * TODO#7-1 읽기 전용 transactional 설정 합니다.
     */
    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //TODO#7-2 member 조회, username은 로그인할 떄 username으로 설정한 email 입니다.
        Optional<Member> memberOptional = memberRepository.findByMbEmail(username);

        //TODO#7-3 회원이 존재하지 않다면 UsernameNotFoundException이 발생 합니다.
        if(memberOptional.isEmpty()){
            throw new UsernameNotFoundException("User with email :%s not found".formatted(username));
        }
        //TODO#7-4 조회한 member entity를 memberResponse객체로 변환 합니다.
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

        log.debug("memberResponse: {}", memberResponse);

        /**
         * TODO#7-5 blogMemberMappingRepository를 이용해서 member<-mbNo에 연결되어 있는 모든 블로그 맵핑 데이터 조회
         * - 다음과 같은 query 작동되도록 method를 작성 합니다.
         * - member table 1번 이상 join 되어서는 안됨니다.
         * - @EntityGraph or join fetch를 이용해서 구현 합니다.
         * select
         *         bmm1_0.blog_member_id,
         *         bmm1_0.blog_id,
         *         b1_0.blog_id,
         *         b1_0.blog_description,
         *         b1_0.blog_fid,
         *         b1_0.blog_is_public,
         *         b1_0.blog_main,
         *         b1_0.blog_mb_nickname,
         *         b1_0.blog_name,
         *         b1_0.created_at,
         *         b1_0.updated_at,
         *         bmm1_0.mb_no,
         *         bmm1_0.role_id
         *     from
         *         blog_member_mappings bmm1_0
         *     left join
         *         members m1_0
         *             on m1_0.mb_no=bmm1_0.mb_no
         *     join
         *         blogs b1_0
         *             on b1_0.blog_id=bmm1_0.blog_id
         *     where
         *         m1_0.mb_no=?
         *
         */
        List<BlogMemberMapping> blogMemberMappings =  blogMemberMappingRepository.findBlogMemberMappingsByMember_MbNo(member.getMbNo());

        log.debug("blogMemberMappings: {}", blogMemberMappings);

        List<MemberGrantedAuthority> memberGrantedAuthorities = new ArrayList<>();

        for (BlogMemberMapping blogMemberMapping : blogMemberMappings) {
          //TODO#7-6 MemberGrantedAuthority 생성 후 memberGrantedAuthorities에 추가 합니다.
            MemberGrantedAuthority memberGrantedAuthority = new MemberGrantedAuthority(
                    blogMemberMapping.getBlog().getBlogId(),
                    blogMemberMapping.getBlog().getBlogFid(),
                    blogMemberMapping.getRole().getRoleId(),
                    blogMemberMapping.getRole().getRoleName()
            );
            memberGrantedAuthorities.add(memberGrantedAuthority);
        }

        //TODO#7-7 MemberDetails 객체를 생성 후 반환 합니다.
        return new MemberDetails(memberResponse,memberGrantedAuthorities, member.getMbPassword());
    }
}
