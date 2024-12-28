package com.nhnacademy.blog.blogmember.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.dto.BlogMemberRegisterRequest;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.blogmember.service.BlogMemberService;
import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.role.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

//@Service
@Transactional(readOnly = true)
public class BlogMemberServiceImpl implements BlogMemberService {

    public static final String BEAN_NAME="blogMemberService";
    private final RoleRepository roleRepository;
    private final BlogMemberMappingRepository blogMemberMappingRepository;
    private final BlogRepository blogRepository;
    private final MemberRepository memberRepository;

    public BlogMemberServiceImpl(
            BlogMemberMappingRepository blogMemberMappingRepository,
            RoleRepository roleRepository,
            BlogRepository blogRepository,
            MemberRepository memberRepository
    ) {
        this.blogMemberMappingRepository = blogMemberMappingRepository;
        this.blogRepository = blogRepository;
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    @Override
    public void registerBlogMember(BlogMemberRegisterRequest blogMemberRegisterRequest) {

        checkBlog(blogMemberRegisterRequest.getBlogId());
        checkRole(blogMemberRegisterRequest.getRoleId());
        checkMember(blogMemberRegisterRequest.getMbNo());

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(
                blogMemberRegisterRequest.getMbNo(),
                blogMemberRegisterRequest.getBlogId(),
                blogMemberRegisterRequest.getRoleId());

        blogMemberMappingRepository.save(blogMemberMapping);

    }

    /**
     *
     * @param mbNo -블로그에서 삭제하려고하는 회원 아이디 ( 로그인한 회원아이디 아님 )
     * @param blogId - 블로그 아이디
     */
    @Transactional
    @Override
    public void removeBlogMember(Long mbNo,Long blogId) {

        checkMember(mbNo);
        checkBlog(blogId);
        long blogMemberId = getBlogMemberIdIfOwner(blogId);

        blogMemberMappingRepository.deleteByBlogMemberMappingId(blogMemberId);
    }

    protected void checkMember(long mbNo){

        boolean isDrawn = memberRepository.isMemberWithdrawn(mbNo);
        boolean existMember = memberRepository.existsByMbNo(mbNo);

        if(!existMember || isDrawn) {
            throw new NotFoundException("member:%s not found".formatted(mbNo));
        }
    }

    protected void checkBlog(long blogId){
        boolean existBlog = blogRepository.existByBlogId(blogId);

        if(!existBlog){
            throw new NotFoundException("blog : %S  does not exist".formatted(blogId));
        }
    }

    protected void checkRole(String roleId){
        boolean existRoleId = roleRepository.existsByRoleId(roleId);

        if(!existRoleId) {
            throw new BadRequestException("invalid roleId : %s".formatted(roleId));
        }
    }

    /**
     * blogMemberId 구하기, 해당 블로그의 owner인지 체크 합니다.
     * @param blogId
     * @return blogMemberId
     */
    protected long getBlogMemberIdIfOwner(long blogId){

        //mbNo는 요청한 사용자의 아이디(로그인한 사용자)
        Long mbNo = MemberThreadLocal.getMemberNo();
        if(Objects.isNull(mbNo)){
            throw new ForbiddenException();
        }

        Optional<BlogMemberMapping> blogMemberMappingOptional = blogMemberMappingRepository.findByMbNoAndBlogId(mbNo, blogId);

        if(blogMemberMappingOptional.isEmpty()) {
            throw  new ForbiddenException();
        }

        if(!blogMemberMappingOptional.get().getRoleId().equals("ROLE_OWNER")){
            throw new ForbiddenException();
        }

        return blogMemberMappingOptional.get().getMbNo();
    }
}
