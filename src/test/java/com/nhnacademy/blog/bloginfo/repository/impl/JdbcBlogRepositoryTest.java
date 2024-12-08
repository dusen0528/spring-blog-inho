package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequestDto;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import org.junit.jupiter.api.*;

import java.util.Optional;

class JdbcBlogRepositoryTest {
    static BlogRepository jdbcBlogRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        jdbcBlogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
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
    @DisplayName("블로그정보 저장(생성)")
    void save() {
        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        Optional<Blog> blogOptional = jdbcBlogRepository.findByBlogId(blog.getBlogId());

        Assertions.assertAll(
                ()-> Assertions.assertNotNull(blog.getBlogId()),
                ()->Assertions.assertEquals(blog.getBlogName(), blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals(blog.getBlogMbNickname(), blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals(blog.getBlogDescription(), blogOptional.get().getBlogDescription())
        );

    }

    @Test
    @DisplayName("블로그정보 수정")
    void update() {

        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        BlogUpdateRequestDto blogUpdateRequestDto = new BlogUpdateRequestDto(
                blog.getBlogId(),
                 "블로그네임-수정",
            "블로그별명-수정",
             "블로그설명-수정"
        );

        jdbcBlogRepository.update(blogUpdateRequestDto);
        Optional<Blog> blogOptional = jdbcBlogRepository.findByBlogId(blog.getBlogId());

        Assertions.assertAll(
            ()->Assertions.assertEquals(blogUpdateRequestDto.getBlogName(),blogOptional.get().getBlogName()),
            ()->Assertions.assertEquals(blogUpdateRequestDto.getBlogMbNickname(),blogOptional.get().getBlogMbNickname()),
            ()->Assertions.assertEquals(blogUpdateRequestDto.getBlogDescription(),blogOptional.get().getBlogDescription())
        );
    }

    @Test
    @DisplayName("blog삭제")
    void delete() {
        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);
        int actual = jdbcBlogRepository.delete(blog.getBlogId());

        Optional<Blog> dbBlog =  jdbcBlogRepository.findByBlogId(blog.getBlogId());
        Assertions.assertAll(
                ()-> Assertions.assertEquals(1,actual),
                ()-> Assertions.assertTrue(dbBlog.isEmpty())
        );
    }

    @Test
    void findByBlogInfoId() {

        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        Optional<Blog> blogOptional = jdbcBlogRepository.findByBlogId(blog.getBlogId());

        Assertions.assertAll(
                ()-> Assertions.assertNotNull(blog.getBlogId()),
                ()->Assertions.assertEquals(blog.getBlogName(), blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals(blog.getBlogMbNickname(), blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals(blog.getBlogDescription(), blogOptional.get().getBlogDescription())
        );

    }

    @Test
    @DisplayName("블로그존재여부:true")
    void existByBlogId() {
        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        boolean actual = jdbcBlogRepository.existByBlogId(blog.getBlogId());
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("블로그존재여부:false")
    void notExistByBlogId() {
        boolean actual = jdbcBlogRepository.existByBlogId(1l);
        Assertions.assertFalse(actual);
    }
}