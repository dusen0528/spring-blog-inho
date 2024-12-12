package com.nhnacademy.blog.bloginfo.service.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogCreateRequest;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.service.BlogInfoService;
import com.nhnacademy.blog.blogmember.repository.BlogMembersMappingRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
class BlogInfoServiceImplTest {

    BlogRepository blogRepository;
    BlogMembersMappingRepository blogMembersMappingRepository;

    BlogInfoService blogInfoService;

    @BeforeEach
    void setUp() {
        blogRepository = Mockito.mock(BlogRepository.class);
        blogMembersMappingRepository = Mockito.mock(BlogMembersMappingRepository.class);
        blogInfoService = new BlogInfoServiceImpl(blogRepository, blogMembersMappingRepository);
    }


    @Test
    @DisplayName("blog 생성")
    void createBlog() {

        //Long mbNo, String blogFid, String blogName, String blogMbNickname, String blogDescription
        BlogCreateRequest blogCreateRequest = new BlogCreateRequest(1L,"blog-fid", "marco's blog", "nhn-academy-marco","welcome to my blog!!");

        //mainBlog 존재여부 체크
        Mockito.when(blogRepository.existMainBlogByMbNo(Mockito.anyLong())).thenReturn(false);
        //blogFid 중복체크
        Mockito.when(blogRepository.existByBlogFid(Mockito.anyString())).thenReturn(false);

        Blog blog = Blog.ofExistingBlogInfo(
                1L,
                blogCreateRequest.getBlogFid(),
                true,
                blogCreateRequest.getBlogName(),
                blogCreateRequest.getBlogMbNickname(),
                blogCreateRequest.getBlogDescription(),
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        //blog 생성
        Mockito.doAnswer(invocationOnMock -> {
            Blog dbBlog = invocationOnMock.getArgument(0);
            //save() 호출시 1L로 변경
            Field field = dbBlog.getClass().getDeclaredField("blogId");
            field.setAccessible(true);
            field.set(dbBlog,1L);
            log.debug("dbBlog: {}", dbBlog);
           return null;
        }).when(blogRepository).save(Mockito.any(Blog.class));

        //블로그 조회
        Mockito.when(blogRepository.findByBlogId(Mockito.anyLong())).thenReturn(Optional.of(blog));

        blogInfoService.createBlog(blogCreateRequest);

        Mockito.verify(blogRepository, Mockito.times(1)).existMainBlogByMbNo(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogFid(Mockito.anyString());
        Mockito.verify(blogRepository, Mockito.times(1)).findByBlogId(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.times(1)).save(Mockito.any(Blog.class));
    }

    @Test
    void updateBlog() {
    }

    @Test
    void updateBlogVisibility() {
    }

    @Test
    void getBlog() {
    }
}