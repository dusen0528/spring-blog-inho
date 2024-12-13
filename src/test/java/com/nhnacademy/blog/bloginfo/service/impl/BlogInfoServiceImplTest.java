package com.nhnacademy.blog.bloginfo.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogCreateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogVisibilityUpdateRequest;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.service.BlogInfoService;
import com.nhnacademy.blog.blogmember.domain.BlogMembersMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMembersMappingRepository;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
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

        MemberThreadLocal.setMemberNo(1L);
    }

    @AfterEach
    void tearDown(){
        MemberThreadLocal.removeMemberNo();
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
    @DisplayName("blog 생성 - blogFid 중복")
    void createBlog_exception_case1() {

        //Long mbNo, String blogFid, String blogName, String blogMbNickname, String blogDescription
        BlogCreateRequest blogCreateRequest = new BlogCreateRequest(1L,"blog-fid", "marco's blog", "nhn-academy-marco","welcome to my blog!!");

        //mainBlog 존재여부 체크
        Mockito.when(blogRepository.existMainBlogByMbNo(Mockito.anyLong())).thenReturn(false);
        //blogFid 중복체크
        Mockito.when(blogRepository.existByBlogFid(Mockito.anyString())).thenReturn(true);

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

        Throwable throwable = Assertions.assertThrows(ConflictException.class, () -> blogInfoService.createBlog(blogCreateRequest));
        log.debug("exception: {}, message:{}",throwable.getClass().getSimpleName(),throwable.getMessage());

        Mockito.verify(blogRepository, Mockito.times(1)).existMainBlogByMbNo(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogFid(Mockito.anyString());
        Mockito.verify(blogRepository, Mockito.never()).save(Mockito.any(Blog.class));
    }

    @Test
    @DisplayName("블로그 수정")
    void updateBlog() {

        BlogUpdateRequest blogUpdateRequest = new BlogUpdateRequest(
                1L,
                false,
                "marco's blog",
                "nhn-academy-marco",
                "welcome to my blog!!"
        );

        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );

        //블로그 존재여부 체크
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogMembersMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMembersMapping));

        blogInfoService.updateBlog(blogUpdateRequest);

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.times(1)).update(Mockito.any(BlogUpdateRequest.class));

    }

    @Test
    @DisplayName("블로그 수정  : 권한 없음(ROLE_MEMBER)")
    void updateBlog_exception_case1() {

        BlogUpdateRequest blogUpdateRequest = new BlogUpdateRequest(
                1L,
                false,
                "marco's blog",
                "nhn-academy-marco",
                "welcome to my blog!!"
        );

        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_MEMBER"
        );

        //블로그 존재여부 체크
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogMembersMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMembersMapping));

        Assertions.assertThrows(ForbiddenException.class,()->{
            blogInfoService.updateBlog(blogUpdateRequest);
        });

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.never()).update(Mockito.any(BlogUpdateRequest.class));

    }

    @Test
    @DisplayName("블로그 수정 : 대표블로그 수정")
    void updateMainBlog() {

        BlogUpdateRequest blogUpdateRequest = new BlogUpdateRequest(
                1L,
                true, //메인블로그 true
                "marco's blog",
                "nhn-academy-marco",
                "welcome to my blog!!"
        );

        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );
        List<BlogResponse> blogResponseList = List.of(
                new BlogResponse(
                        1L,
                        "marco-1",
                        true,
                        "blogName",
                        "blogMbNickName",
                        "blogDescription",
                        true,
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now()
                ),
                new BlogResponse(
                        2L,
                        "marco-2",
                        true,
                        "blogName",
                        "blogMbNickName",
                        "blogDescription",
                        true,
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now()
                ),

                new BlogResponse(
                        3L,
                        "marco-3",
                        false,
                        "blogName",
                        "blogMbNickName",
                        "blogDescription",
                        true,
                        LocalDateTime.now().minusDays(30),
                        LocalDateTime.now()
                )
        );

        //블로그 존재여부 체크
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        //MbNo blogId로 blogMember 조회
        Mockito.when(blogMembersMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMembersMapping));
        Mockito.when(blogRepository.findAllBlogs(Mockito.anyLong(),Mockito.anyString())).thenReturn(blogResponseList);

        blogInfoService.updateBlog(blogUpdateRequest);

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.times(1)).findAllBlogs(Mockito.anyLong(), Mockito.anyString());
        //최소 1회이상 호출
        Mockito.verify(blogRepository, Mockito.atLeast(1)).updateBlogMain(Mockito.anyLong(),Mockito.anyBoolean());
        Mockito.verify(blogRepository, Mockito.times(1)).update(Mockito.any(BlogUpdateRequest.class));

    }

    @Test
    @DisplayName("블로그 공개 : true")
    void updateBlogVisibility() {

        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_OWNER"
        );

        BlogVisibilityUpdateRequest blogVisibilityUpdateRequest = new BlogVisibilityUpdateRequest(1L,true);

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogMembersMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMembersMapping));

        blogInfoService.updateBlogVisibility(blogVisibilityUpdateRequest);

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.times(1)).updateByBlogIsPublic(Mockito.anyLong(),Mockito.anyBoolean());

    }

    @Test
    @DisplayName("블로그 공개 : 권한없음")
    void updateBlogVisibility_exception_case1() {

        BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofExistingBlogMemberMapping(
                1L,
                1L,
                1L,
                "ROLE_MEMBER"
        );

        BlogVisibilityUpdateRequest blogVisibilityUpdateRequest = new BlogVisibilityUpdateRequest(1L,true);

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogMembersMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMembersMapping));

        Assertions.assertThrows(ForbiddenException.class,()->{
            blogInfoService.updateBlogVisibility(blogVisibilityUpdateRequest);
        });

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(blogRepository, Mockito.never()).updateByBlogIsPublic(Mockito.anyLong(),Mockito.anyBoolean());

    }

    @Test
    @DisplayName("블로그 조회")
    void getBlog() {

        Blog blog = Blog.ofExistingBlogInfo(
                1L,
                "marco",
                true,
                "marco's blog",
                "nhn-academy-marco",
                "welcome to my blog!!",
                true,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        Mockito.when(blogRepository.findByBlogId(Mockito.anyLong())).thenReturn(Optional.of(blog));
        blogInfoService.getBlog(1L);

        Mockito.verify(blogRepository, Mockito.times(1)).findByBlogId(Mockito.anyLong());
    }

    @Test
    @DisplayName("블로그 조회 - 존재하지 않는 블로그")
    void getBlog_exception_case1() {
        Mockito.when(blogRepository.findByBlogId(Mockito.anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,()->{
            blogInfoService.getBlog(1L);
        });

        Mockito.verify(blogRepository, Mockito.times(1)).findByBlogId(Mockito.anyLong());
    }
}