package com.nhnacademy.blog.blogmember.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.JpaBlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.common.config.init.CustomContextInitializer;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional
@ContextConfiguration(classes = {ApplicationConfig.class},
        loader = AnnotationConfigContextLoader.class,
        initializers = CustomContextInitializer.class
)
@ExtendWith(SpringExtension.class)
class JdbcBlogMemberMappingRepositoryTest {

    @Autowired
    JpaBlogRepository blogRepository;

    @Autowired
    BlogMemberMappingRepository blogMemberMappingRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    @DisplayName("블로그+회원 : 연결")
    void save() {

            //1.블로그사용자 생성
            Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
            memberRepository.save(member);

            //2.블로그 생성
            Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
            blogRepository.save(blog);

            //3.카테고리 생성
            Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
            categoryRepository.save(category);

            //4.권한 생성
            Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
            roleRepository.save(role);

            //5.블로그 사용자 연결
            BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(),  role.getRoleId());
            blogMemberMappingRepository.save(blogMemberMapping);

            Optional<BlogMemberMapping> blogMemberMappingOptional = blogMemberMappingRepository.findByBlogMemberId(blogMemberMapping.getBlogMemberId());
            Assertions.assertTrue(blogMemberMappingOptional.isPresent());
            Assertions.assertAll(
                    ()->Assertions.assertEquals(member.getMbNo(),blogMemberMappingOptional.get().getMbNo()),
                    ()->Assertions.assertEquals(blog.getBlogId(),blogMemberMappingOptional.get().getBlogId()),
                    ()->Assertions.assertEquals(role.getRoleId(),blogMemberMappingOptional.get().getRoleId())
            );

    }

    @Test
    @DisplayName("블로그+회원 : 연결삭제")
    void deleteByBlogMemberMappingId() {
        //given

        //1.블로그사용자 생성
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        //2.블로그 생성
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //3.카테고리 생성
        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        categoryRepository.save(category);

        //4.권한 생성
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        //5.블로그 사용자 연결
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(), role.getRoleId());
        blogMemberMappingRepository.save(blogMemberMapping);

        //when
        blogMemberMappingRepository.deleteByBlogMemberMappingId(blogMemberMapping.getBlogMemberId());

        //then
        Optional<BlogMemberMapping> blogMemberMappingOptional = blogMemberMappingRepository.findByBlogMemberId(blogMemberMapping.getBlogMemberId());

        log.debug("blogMemberMappingOptional: {}", blogMemberMappingOptional.isPresent());
        Assertions.assertTrue(blogMemberMappingOptional.isEmpty());

    }

    @Test
    @DisplayName("mb_id + blog_id 이용한 조회")
    void findByMbNoAndBlogId(){

        //1.블로그사용자 생성
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        //2.블로그 생성
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //3.카테고리 생성
        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        categoryRepository.save(category);

        //4.권한 생성
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        //5.블로그 사용자 연결
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(),  role.getRoleId());
        blogMemberMappingRepository.save(blogMemberMapping);

        Optional<BlogMemberMapping> blogMemberMappingOptional = blogMemberMappingRepository.findByMbNoAndBlogId(member.getMbNo(), blog.getBlogId());

        Assertions.assertTrue(blogMemberMappingOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(member.getMbNo(),blogMemberMappingOptional.get().getMbNo()),
                ()->Assertions.assertEquals(blog.getBlogId(),blogMemberMappingOptional.get().getBlogId()),
                ()->Assertions.assertEquals(role.getRoleId(),blogMemberMappingOptional.get().getRoleId())
        );
    }
}