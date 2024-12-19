package com.nhnacademy.blog.post.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.repository.impl.JdbcMemberRepository;

import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.repository.PostRepository;
import org.junit.jupiter.api.*;

import java.util.Optional;

class JdbcPostRepositoryTest {
    static MemberRepository memberRepository;
    static PostRepository postRepository;
    static BlogRepository blogRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        memberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
        postRepository = (PostRepository) context.getBean(JdbcPostRepository.BEAN_NAME);
        blogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
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
    @DisplayName("post 작성")
    void save() {

        //given
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);
        Optional<Post> postOptional = postRepository.findByPostId(post.getPostId());

        //then
        Assertions.assertTrue(postOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(post.getPostId(), postOptional.get().getPostId()),
                ()->Assertions.assertEquals("post-title", postOptional.get().getPostTitle()),
                ()->Assertions.assertEquals("post-content", postOptional.get().getPostContent()),
                ()->Assertions.assertTrue(postOptional.get().isPostIsPublic()),
                ()->Assertions.assertNotNull(postOptional.get().getCreatedAt())
        );

    }

    @Test
    void update() {

    }

    @Test
    void deleteByPostId() {
    }

    @Test
    void updateByPostIdAndPostIsPublic() {
    }

    @Test
    void existsByPostId() {
    }

    @Test
    void findByPostId() {
    }

    @Test
    void findAllByPageableAndPostSearchRequest() {
    }

    @Test
    void totalRowsByPostSearchRequest() {
    }
}