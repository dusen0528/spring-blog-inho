package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
class JdbcBlogRepositoryTest {

    @Autowired
    BlogRepository blogRepository;

    @Test
    @DisplayName("블로그정보 저장(생성)")
    void save() {

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Optional<Blog> blogOptional = blogRepository.findByBlogId(blog.getBlogId());

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
        blogRepository.save(blog);

        BlogUpdateRequest blogUpdateRequest = new BlogUpdateRequest(
                blog.getBlogId(),
                false,
                "블로그네임-수정",
                "블로그별명-수정",
                "블로그설명-수정"
        );

        blogRepository.update(blogUpdateRequest);
        Optional<Blog> blogOptional = blogRepository.findByBlogId(blog.getBlogId());

        Assertions.assertTrue(blogOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(blogUpdateRequest.isBlogMain(), blogOptional.get().isBlogMain()),
                ()->Assertions.assertEquals(blogUpdateRequest.getBlogName(),blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals(blogUpdateRequest.getBlogMbNickname(),blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals(blogUpdateRequest.getBlogDescription(),blogOptional.get().getBlogDescription())
        );
    }

    @Test
    @DisplayName("blog삭제")
    void delete() {
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);
        blogRepository.deleteByBlogId(blog.getBlogId());

        boolean actual =blogRepository.existByBlogId(blog.getBlogId());
        Assertions.assertFalse(actual);
    }

    @Test
    void findByBlogInfoId() {

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Optional<Blog> blogOptional = blogRepository.findByBlogId(blog.getBlogId());

        Assertions.assertTrue(blogOptional.isPresent());
        Assertions.assertAll(
                ()-> Assertions.assertNotNull(blog.getBlogId()),
                ()->Assertions.assertTrue(blogOptional.get().isBlogMain()),
                ()->Assertions.assertEquals(blog.getBlogName(), blogOptional.get().getBlogName()),
                ()->Assertions.assertEquals(blog.getBlogMbNickname(), blogOptional.get().getBlogMbNickname()),
                ()->Assertions.assertEquals(blog.getBlogDescription(), blogOptional.get().getBlogDescription()),
                ()->Assertions.assertTrue(blogOptional.get().getBlogIsPublic())
        );
    }

    @Test
    @DisplayName("블로그존재여부-by-blogId:true")
    void existByBlogId() {
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        boolean actual = blogRepository.existByBlogId(blog.getBlogId());
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("블로그존재여부-by-blogId:false")
    void notExistByBlogId() {
        boolean actual = blogRepository.existByBlogId(1);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("블로그존재여부-by-blogFid:true")
    void existByBlogFid(){
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        boolean actual = blogRepository.existByBlogFid(blog.getBlogFid());
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("블로그존재여부-by-blogFid:false")
    void notExistByBlogFid(){
        boolean actual = blogRepository.existByBlogFid("marco");
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("블로그 공개여부 설정 : 공개")
    void updateByBlogIsPublic_true(){
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        blogRepository.updateByBlogIsPublic(blog.getBlogId(),true);
        Optional<Blog> actual = blogRepository.findByBlogId(blog.getBlogId());

        //then
        Assertions.assertTrue(actual.get().getBlogIsPublic());

    }

    @Test
    @DisplayName("블로그 공개여부 설정 : 비공개")
    void updateByBlogIsPublic_false(){
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        blogRepository.updateByBlogIsPublic(blog.getBlogId(),false);
        Optional<Blog> actual = blogRepository.findByBlogId(blog.getBlogId());

        //then
        Assertions.assertFalse(actual.get().getBlogIsPublic());
    }

}