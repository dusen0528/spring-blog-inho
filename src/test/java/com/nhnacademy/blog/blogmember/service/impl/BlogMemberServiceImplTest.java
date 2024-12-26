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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

class BlogMemberServiceImplTest {

    BlogMemberService blogMemberService;
    BlogMemberMappingRepository blogMemberMappingRepository;
    RoleRepository roleRepository;
    MemberRepository memberRepository;
    BlogRepository blogRepository;

    @BeforeEach
    void setUp(){
        blogMemberMappingRepository = Mockito.mock(BlogMemberMappingRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        memberRepository = Mockito.mock(MemberRepository.class);
        blogRepository = Mockito.mock(BlogRepository.class);

        blogMemberService = new BlogMemberServiceImpl(blogMemberMappingRepository,roleRepository,blogRepository,memberRepository);
    }

    @Test
    @DisplayName("블로그에 회원연결")
    void registerBlogMember() {
        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        //role 권한 존재여부
        Mockito.when(roleRepository.existsByRoleId(Mockito.anyString())).thenReturn(true);
        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        BlogMemberRegisterRequest blogMemberRegisterRequest = new BlogMemberRegisterRequest(
                1L,
                1L,
                "ROLE_OWNER"
        );

        blogMemberService.registerBlogMember(blogMemberRegisterRequest);

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).save(Mockito.any(BlogMemberMapping.class));
    }

    @Test
    @DisplayName("블로그에 회원연결 예외 - blog존재하지 않을 때")
    void registerBlogMember_exception_case1() {
        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);
        //role 권한 존재여부
        Mockito.when(roleRepository.existsByRoleId(Mockito.anyString())).thenReturn(true);
        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        BlogMemberRegisterRequest blogMemberRegisterRequest = new BlogMemberRegisterRequest(
                1L,
                1L,
                "ROLE_OWNER"
        );
        Assertions.assertThrows(NotFoundException.class,()->{
            blogMemberService.registerBlogMember(blogMemberRegisterRequest);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).save(Mockito.any(BlogMemberMapping.class));
    }

    @Test
    @DisplayName("블로그에 회원연결 예외 - roleId가 존재하지 않을 때")
    void registerBlogMember_exception_case2() {
        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        //role 권한 존재여부
        Mockito.when(roleRepository.existsByRoleId(Mockito.anyString())).thenReturn(false);
        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        BlogMemberRegisterRequest blogMemberRegisterRequest = new BlogMemberRegisterRequest(
                1L,
                1L,
                "ROLE_OWNER"
        );
        Assertions.assertThrows(BadRequestException.class,()->{
            blogMemberService.registerBlogMember(blogMemberRegisterRequest);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).save(Mockito.any(BlogMemberMapping.class));
    }

    @Test
    @DisplayName("블로그에 회원연결 예외 - 존재하지 않는 회원")
    void registerBlogMember_exception_case3() {
        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        //role 권한 존재여부
        Mockito.when(roleRepository.existsByRoleId(Mockito.anyString())).thenReturn(true);
        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(false);
        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        BlogMemberRegisterRequest blogMemberRegisterRequest = new BlogMemberRegisterRequest(
                1L,
                1L,
                "ROLE_OWNER"
        );

        Assertions.assertThrows(NotFoundException.class,()->{
            blogMemberService.registerBlogMember(blogMemberRegisterRequest);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).save(Mockito.any(BlogMemberMapping.class));
    }

    @Test
    @DisplayName("블로그에 회원연결 예외 - 탈퇴한 회원")
    void registerBlogMember_exception_case4() {
        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        //role 권한 존재여부
        Mockito.when(roleRepository.existsByRoleId(Mockito.anyString())).thenReturn(true);
        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);
        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(true);

        BlogMemberRegisterRequest blogMemberRegisterRequest = new BlogMemberRegisterRequest(
                1L,
                1L,
                "ROLE_OWNER"
        );

        Assertions.assertThrows(NotFoundException.class,()->{
            blogMemberService.registerBlogMember(blogMemberRegisterRequest);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).save(Mockito.any(BlogMemberMapping.class));
    }

    @Test
    @DisplayName("블로그에 연결된 회원삭제")
    void removeBlogMember() {
        MemberThreadLocal.setMemberNo(1L);

        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);

        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );

        //블로그 맴버를 삭제할 수 있는 권한 여부
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        blogMemberService.removeBlogMember(1L,1L);

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).deleteByBlogMemberMappingId(Mockito.anyLong());

    }


    @Test
    @DisplayName("블로그에 연결된 회원삭제 예외 : 존재하지 않는 회원")
    void removeBlogMember_exception_case1() {
        MemberThreadLocal.setMemberNo(1L);

        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(false);

        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );

        //블로그 맴버를 삭제할 수 있는 권한 여부
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Assertions.assertThrows(NotFoundException.class,()->{
            blogMemberService.removeBlogMember(1L,1L);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).deleteByBlogMemberMappingId(Mockito.anyLong());

    }


    @Test
    @DisplayName("블로그에 연결된 회원삭제 예외 : 탈퇴한 회원")
    void removeBlogMember_exception_case2() {
        MemberThreadLocal.setMemberNo(1L);

        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);

        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(true);

        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );

        //블로그 맴버를 삭제할 수 있는 권한 여부
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Assertions.assertThrows(NotFoundException.class,()->{
            blogMemberService.removeBlogMember(1L,1L);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).deleteByBlogMemberMappingId(Mockito.anyLong());

    }

    @Test
    @DisplayName("블로그에 연결된 회원삭제 예외 : 존재하지 않는 블로그")
    void removeBlogMember_exception_case3() {
        MemberThreadLocal.setMemberNo(1L);

        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);

        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );

        //블로그 맴버를 삭제할 수 있는 권한 여부
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Assertions.assertThrows(NotFoundException.class,()->{
            blogMemberService.removeBlogMember(1L,1L);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).deleteByBlogMemberMappingId(Mockito.anyLong());

    }

    @Test
    @DisplayName("블로그에 연결된 회원삭제 예외 : 권한(ROLE_OWNER) 없음")
    void removeBlogMember_exception_case4() {
        MemberThreadLocal.setMemberNo(1L);

        //회원  존재여부
        Mockito.when(memberRepository.existsByMbNo(Mockito.anyLong())).thenReturn(true);

        //회원 탈퇴여부
        Mockito.when(memberRepository.isMemberWithdrawn(Mockito.anyLong())).thenReturn(false);

        //blog 존재여부
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_MEMBER"
        );

        //블로그 맴버를 삭제할 수 있는 권한 여부
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Assertions.assertThrows(ForbiddenException.class,()->{
            blogMemberService.removeBlogMember(1L,1L);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.never()).deleteByBlogMemberMappingId(Mockito.anyLong());

    }
}