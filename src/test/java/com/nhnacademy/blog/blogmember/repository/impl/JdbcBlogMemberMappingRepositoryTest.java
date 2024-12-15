package com.nhnacademy.blog.blogmember.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.category.repository.impl.JdbcCategoryRepository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.repository.impl.JdbcMemberRepository;
import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.repository.RoleRepository;
import com.nhnacademy.blog.role.repository.impl.JdbcRoleRepository;
import org.junit.jupiter.api.*;

import java.util.Optional;

class JdbcBlogMemberMappingRepositoryTest {

    static BlogRepository jdbcBlogRepository;
    static BlogMemberMappingRepository jdbcBlogMemberMappingRepository;
    static CategoryRepository jdbcCategoryRepository;
    static MemberRepository jdbcMemberRepository;
    static RoleRepository jdbcRoleRepository;

    @BeforeAll
    static void beforeAll() {

        Context context = ContextHolder.getApplicationContext();
        jdbcBlogMemberMappingRepository = (BlogMemberMappingRepository) context.getBean(JdbcBlogMemberMappingRepository.BEAN_NAME);
        jdbcBlogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
        jdbcCategoryRepository = (CategoryRepository) context.getBean(JdbcCategoryRepository.BEAN_NAME);
        jdbcMemberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
        jdbcRoleRepository = (RoleRepository) context.getBean(JdbcRoleRepository.BEAN_NAME);
    }

    @BeforeEach
    void setUp(){
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown(){
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("블로그+회원 : 연결")
    void save() {

            //1.블로그사용자 생성
            Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
            jdbcMemberRepository.save(member);

            //2.블로그 생성
            Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
            jdbcBlogRepository.save(blog);

            //3.카테고리 생성
            Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
            jdbcCategoryRepository.save(category);

            //4.권한 생성
            Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
            jdbcRoleRepository.save(role);

            //5.블로그 사용자 연결
            BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(),  role.getRoleId());
            jdbcBlogMemberMappingRepository.save(blogMemberMapping);

            Optional<BlogMemberMapping> blogMemberMappingOptional = jdbcBlogMemberMappingRepository.findByBlogMemberId(blogMemberMapping.getBlogMemberId());
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
        jdbcMemberRepository.save(member);

        //2.블로그 생성
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        //3.카테고리 생성
        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        jdbcCategoryRepository.save(category);

        //4.권한 생성
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        jdbcRoleRepository.save(role);

        //5.블로그 사용자 연결
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(), role.getRoleId());
        jdbcBlogMemberMappingRepository.save(blogMemberMapping);

        //when
        jdbcBlogMemberMappingRepository.deleteByBlogMemberMappingId(blogMemberMapping.getBlogMemberId());

        //then
        boolean actual = jdbcBlogMemberMappingRepository.findByBlogMemberId(blogMemberMapping.getBlogMemberId()).isEmpty();
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("mb_id + blog_id 이용한 조회")
    void findByMbNoAndBlogId(){

        //1.블로그사용자 생성
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        jdbcMemberRepository.save(member);

        //2.블로그 생성
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        //3.카테고리 생성
        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        jdbcCategoryRepository.save(category);

        //4.권한 생성
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        jdbcRoleRepository.save(role);

        //5.블로그 사용자 연결
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(),  role.getRoleId());
        jdbcBlogMemberMappingRepository.save(blogMemberMapping);

        Optional<BlogMemberMapping> blogMemberMappingOptional = jdbcBlogMemberMappingRepository.findByMbNoAndBlogId(member.getMbNo(), blog.getBlogId());

        Assertions.assertTrue(blogMemberMappingOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(member.getMbNo(),blogMemberMappingOptional.get().getMbNo()),
                ()->Assertions.assertEquals(blog.getBlogId(),blogMemberMappingOptional.get().getBlogId()),
                ()->Assertions.assertEquals(role.getRoleId(),blogMemberMappingOptional.get().getRoleId())
        );

    }
}