package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.JpaBlogRepository;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.common.config.init.CustomContextInitializer;
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
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class},
        loader = AnnotationConfigContextLoader.class,
        initializers = CustomContextInitializer.class
)
@Transactional
class JdbcBlogRepositoryTest {

    @Autowired
    JpaBlogRepository blogRepository;

    @Test
    @DisplayName("블로그정보 저장(생성)")
    void save() {

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.saveAndFlush(blog);

        Optional<Blog> blogOptional = blogRepository.findById(blog.getBlogId());

        Assertions.assertTrue(blogOptional.isPresent());

        Assertions.assertAll(
                ()-> Assertions.assertNotNull(blog.getBlogId()),
                ()->Assertions.assertTrue(blogOptional.get().isBlogMain()),
                ()->Assertions.assertEquals(blog.getBlogName(), blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals(blog.getBlogMbNickname(), blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals(blog.getBlogDescription(), blogOptional.get().getBlogDescription())
        );
    }

    @Test
    @DisplayName("블로그정보 수정")
    void update() {

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.saveAndFlush(blog);
        long blogId = blog.getBlogId();

        Optional<Blog> dbBlogOptional = blogRepository.findById(blogId);
        dbBlogOptional.ifPresent(dbBlog ->{
            dbBlog.update("블로그네임-수정",
                    "블로그별명-수정",
                    "블로그설명-수정",
                    false
            );
            blogRepository.saveAndFlush(dbBlog);
        });

        Optional<Blog> blogOptional = blogRepository.findById(blogId);
        log.debug("blogOptional: {}", blogOptional);

        Assertions.assertTrue(blogOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertFalse(blogOptional.get().getBlogIsPublic()),
                ()->Assertions.assertEquals("블로그네임-수정",blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals("블로그별명-수정",blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals("블로그설명-수정",blogOptional.get().getBlogDescription())
        );
    }

    @Test
    @DisplayName("blog삭제")
    void delete() {
        Blog blog = Blog.ofNewBlog("marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        blogRepository.saveAndFlush(blog);
        long blogId = blog.getBlogId();

        blogRepository.deleteById(blogId);

        boolean actual =blogRepository.existsById(blogId);
        Assertions.assertFalse(actual);

    }

    @Test
    void findByBlogInfoId() {
        Blog blog = Blog.ofNewBlog("marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );

        blogRepository.save(blog);
        long blogId = blog.getBlogId();

        Optional<Blog> blogOptional = blogRepository.findById(blogId);

        Assertions.assertTrue(blogOptional.isPresent());
        Assertions.assertAll(
                ()-> Assertions.assertNotNull(blog.getBlogId()),
                ()->Assertions.assertTrue(blogOptional.get().isBlogMain()),
                ()->Assertions.assertEquals("NHN아카데미-blog", blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals("nhn-academy-marco", blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals("NHN아카데미-블로그 입니다.", blogOptional.get().getBlogDescription()),
                ()->Assertions.assertTrue(blogOptional.get().getBlogIsPublic())
        );
    }

    @Test
    @DisplayName("블로그존재여부-by-blogId:true")
    void existByBlogId() {
        Blog blog = Blog.ofNewBlog("marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        blogRepository.saveAndFlush(blog);
        long blogId = blog.getBlogId();

        boolean actual = blogRepository.existsById(blogId);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("블로그존재여부-by-blogId:false")
    void notExistByBlogId() {
        boolean actual = blogRepository.existsById(1L);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("블로그존재여부-by-blogFid:true")
    void existByBlogFid(){
        Blog blog = Blog.ofNewBlog("marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        blogRepository.saveAndFlush(blog);
        String blogFid = blog.getBlogFid();

        boolean actual = blogRepository.existsByBlogFid(blogFid);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("블로그존재여부-by-blogFid:false")
    void notExistByBlogFid(){
        boolean actual = blogRepository.existsByBlogFid("marco");
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("블로그 공개여부 설정 : 공개")
    void updateByBlogIsPublic_true(){
        //given
        Blog blog = Blog.ofNewBlog("marco",
                false,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        blogRepository.saveAndFlush(blog);
        long blogId = blog.getBlogId();

        //when
        blog.enableBlogPublicAccess(true);
        blogRepository.saveAndFlush(blog);

        Optional<Blog> actual = blogRepository.findById(blogId);

        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertTrue(actual.get().getBlogIsPublic());

    }

    @Test
    @DisplayName("블로그 공개여부 설정 : 비공개")
    void updateByBlogIsPublic_false(){
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.saveAndFlush(blog);
        long blogId = blog.getBlogId();

        //when
        blog.enableBlogPublicAccess(false);
        blogRepository.saveAndFlush(blog);

        Optional<Blog> actual = blogRepository.findById(blogId);

        //then
        Assertions.assertTrue(actual.isPresent());
        Assertions.assertFalse(actual.get().getBlogIsPublic());
    }

}