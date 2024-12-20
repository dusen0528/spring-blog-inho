package com.nhnacademy.blog.tagging.repository.impl;

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
import com.nhnacademy.blog.post.repository.impl.JdbcPostRepository;
import com.nhnacademy.blog.tag.domain.Tag;
import com.nhnacademy.blog.tag.repository.TagRepository;
import com.nhnacademy.blog.tag.repository.impl.JdbcTagRepository;
import com.nhnacademy.blog.tagging.domain.PostTag;
import com.nhnacademy.blog.tagging.repository.PostTagRepository;
import org.junit.jupiter.api.*;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcPostTagRepositoryTest {

    static PostTagRepository postTagRepository;
    static MemberRepository memberRepository;
    static BlogRepository blogRepository;
    static PostRepository postRepository;
    static TagRepository tagRepository;

    static long postId;
    static long tagId;
    static long postTagId;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        postTagRepository = (PostTagRepository) context.getBean(JdbcPostTagRepository.BEAN_NAME);
        memberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
        blogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
        postRepository = (PostRepository) context.getBean(JdbcPostRepository.BEAN_NAME);
        tagRepository = (TagRepository) context.getBean(JdbcTagRepository.BEAN_NAME);

        DbConnectionThreadLocal.initialize();
    }

    @AfterAll
    static void afterAll() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @Order(1)
    @DisplayName("post-tag 연결")
    void save() {

        //given
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);
        postId = post.getPostId();

        Optional<Post> postOptional = postRepository.findByPostId(postId);

        Tag tag = Tag.ofNewTag("java");
        tagRepository.save(tag);
        tagId = tag.getTagId();

        PostTag postTag = PostTag.ofNewPostTag(postId,tagId);
        postTagRepository.save(postTag);
        postTagId = postTag.getPostTagId();

        Optional<PostTag> postTagOptional = postTagRepository.findByPostTagId(postTagId);

        Assertions.assertTrue(postTagOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(postTagOptional.get()),
                ()->Assertions.assertEquals(postTagId,postTagOptional.get().getPostTagId()),
                ()-> Assertions.assertEquals(postId,postTagOptional.get().getPostId()),
                ()->Assertions.assertEquals(tagId,postTagOptional.get().getTagId())
        );
    }

    @Test
    @Order(2)
    @DisplayName("post-tag 조회-by-postTagId")
    void findByPostTagId() {

        Optional<PostTag> postTagOptional = postTagRepository.findByPostTagId(postTagId);

        Assertions.assertTrue(postTagOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(postTagOptional.get()),
                ()->Assertions.assertEquals(postTagId,postTagOptional.get().getPostTagId()),
                ()-> Assertions.assertEquals(postId,postTagOptional.get().getPostId()),
                ()->Assertions.assertEquals(tagId,postTagOptional.get().getTagId())
        );

    }

    @Test
    @Order(3)
    @DisplayName("post-tag 조회-by-postId+tagId")
    void findByPostIdAndTagId() {

        Optional<PostTag> postTagOptional = postTagRepository.findByPostIdAndTagId(postId,tagId);

        Assertions.assertTrue(postTagOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(postTagOptional.get()),
                ()->Assertions.assertEquals(postTagId,postTagOptional.get().getPostTagId()),
                ()-> Assertions.assertEquals(postId,postTagOptional.get().getPostId()),
                ()->Assertions.assertEquals(tagId,postTagOptional.get().getTagId())
        );
    }

    @Test
    @Order(4)
    @DisplayName("post-tag 존재여부 -by-postTagId")
    void existsByPostTagId() {
        boolean actual = postTagRepository.existsByPostTagId(postTagId);
        Assertions.assertTrue(actual);
    }

    @Test
    @Order(5)
    @DisplayName("post-tag 존재여부 -by-postId + tagId")
    void existsByPostIdAndTagId() {
        boolean actual = postTagRepository.existsByPostIdAndTagId(postId,tagId);
        Assertions.assertTrue(actual);
    }

    @Test
    @Order(6)
    @DisplayName("post-tag 삭제 - by-postTagId")
    void deleteByPostTagId() {
        postTagRepository.deleteByPostTagId(postTagId);
        boolean actual = postTagRepository.existsByPostTagId(postTagId);
        Assertions.assertFalse(actual);
    }

    @Test
    @Order(7)
    @DisplayName("post-tag 삭제 - by-postId + tagId")
    void deleteByPostIdAndTagId() {

        PostTag postTag = PostTag.ofNewPostTag(postId,tagId);
        postTagRepository.save(postTag);

        postTagRepository.deleteByPostIdAndTagId(postId,tagId);

        boolean actual = postTagRepository.existsByPostIdAndTagId(postId,tagId);
        Assertions.assertFalse(actual);
    }

}