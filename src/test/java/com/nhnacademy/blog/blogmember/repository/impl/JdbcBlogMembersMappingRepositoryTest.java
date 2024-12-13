package com.nhnacademy.blog.blogmember.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMembersMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMembersMappingRepository;
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

class JdbcBlogMembersMappingRepositoryTest {

    static BlogRepository jdbcBlogRepository;
    static BlogMembersMappingRepository jdbcBlogMembersMappingRepository;
    static CategoryRepository jdbcCategoryRepository;
    static MemberRepository jdbcMemberRepository;
    static RoleRepository jdbcRoleRepository;

    @BeforeAll
    static void beforeAll() {

        Context context = ContextHolder.getApplicationContext();
        jdbcBlogMembersMappingRepository = (BlogMembersMappingRepository) context.getBean(JdbcBlogMembersMappingRepository.BEAN_NAME);
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
            BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(),  role.getRoleId());
            jdbcBlogMembersMappingRepository.save(blogMembersMapping);

            Optional<BlogMembersMapping> blogMembersMappingOptional = jdbcBlogMembersMappingRepository.findByBlogMembersId(blogMembersMapping.getBlogMembersId());
            Assertions.assertTrue(blogMembersMappingOptional.isPresent());
            Assertions.assertAll(
                    ()->Assertions.assertEquals(member.getMbNo(),blogMembersMappingOptional.get().getMbNo()),
                    ()->Assertions.assertEquals(blog.getBlogId(),blogMembersMappingOptional.get().getBlogId()),
                    ()->Assertions.assertEquals(role.getRoleId(),blogMembersMappingOptional.get().getRoleId())
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
        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(), role.getRoleId());
        jdbcBlogMembersMappingRepository.save(blogMembersMapping);

        //when
        jdbcBlogMembersMappingRepository.deleteByBlogMemberMappingId(blogMembersMapping.getBlogMembersId());

        //then
        boolean actual = jdbcBlogMembersMappingRepository.findByBlogMembersId(blogMembersMapping.getBlogMembersId()).isEmpty();
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
        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofNewBlogMemberMapping(member.getMbNo(), blog.getBlogId(),  role.getRoleId());
        jdbcBlogMembersMappingRepository.save(blogMembersMapping);

        Optional<BlogMembersMapping> blogMembersMappingOptional = jdbcBlogMembersMappingRepository.findByMbNoAndBlogId(member.getMbNo(), blog.getBlogId());

        Assertions.assertTrue(blogMembersMappingOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(member.getMbNo(),blogMembersMappingOptional.get().getMbNo()),
                ()->Assertions.assertEquals(blog.getBlogId(),blogMembersMappingOptional.get().getBlogId()),
                ()->Assertions.assertEquals(role.getRoleId(),blogMembersMappingOptional.get().getRoleId())
        );

    }
}