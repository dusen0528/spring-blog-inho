package com.nhnacademy.blog.postcategory.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.category.repository.impl.JdbcCategoryRepository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.repository.impl.JdbcMemberRepository;
import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.repository.PostRepository;
import com.nhnacademy.blog.post.repository.impl.JdbcPostRepository;
import com.nhnacademy.blog.postcategory.domain.PostCategory;
import com.nhnacademy.blog.postcategory.repository.PostCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.Optional;
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JdbcPostCategoryRepositoryTest {

    static PostCategoryRepository postCategoryRepository;
    static MemberRepository memberRepository;
    static PostRepository postRepository;
    static BlogRepository blogRepository;
    static CategoryRepository categoryRepository;

    static long postId;
    static long categoryId;
    static long postCategoryId;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        postCategoryRepository = (PostCategoryRepository) context.getBean(JdbcPostCategoryRepository.BEAN_NAME);
        memberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
        postRepository = (PostRepository) context.getBean(JdbcPostRepository.BEAN_NAME);
        blogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
        categoryRepository = (CategoryRepository) context.getBean(JdbcCategoryRepository.BEAN_NAME);

        DbConnectionThreadLocal.initialize();

    }

    @AfterAll
    static void afterAll(){
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("post-category 맵핑")
    @Order(1)
    void save() {

        //회원등록
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        //블로그 생성
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //포스트 등록
        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);
        postId = post.getPostId();

        //카테고리 등록
        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"java",1);
        categoryRepository.save(category);
        categoryId = category.getCategoryId();

        //포스트 - 카테고리 맵핑
        PostCategory postCategory = PostCategory.ofNewPostCategory(postId, categoryId);
        postCategoryRepository.save(postCategory);
        postCategoryId = postCategory.getPostCategoryId();

        Optional<PostCategory> postCategoryOptional = postCategoryRepository.findByPostCategoryId(postCategory.getPostCategoryId());

        Assertions.assertTrue(postCategoryOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(postCategoryId, postCategoryOptional.get().getPostCategoryId()),
                ()->Assertions.assertEquals(categoryId, postCategoryOptional.get().getCategoryId()),
                ()->Assertions.assertEquals(postId,postCategoryOptional.get().getPostId())
        );

    }

    @Test
    @DisplayName("post-category 조회-by-postCategoryId")
    @Order(2)
    void findByPostCategoryId() {

        Optional<PostCategory> postCategoryOptional = postCategoryRepository.findByPostCategoryId(postCategoryId);

        Assertions.assertTrue(postCategoryOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(postCategoryId, postCategoryOptional.get().getPostCategoryId()),
                ()->Assertions.assertEquals(categoryId, postCategoryOptional.get().getCategoryId()),
                ()->Assertions.assertEquals(postId,postCategoryOptional.get().getPostId())
        );
    }

    @Test
    @DisplayName("post-category 조회-by-postId + categoryId")
    @Order(3)
    void findByPostIdAndCategoryId() {
        Optional<PostCategory> postCategoryOptional = postCategoryRepository.findByPostIdAndCategoryId(postId, categoryId);

        Assertions.assertTrue(postCategoryOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(postCategoryId, postCategoryOptional.get().getPostCategoryId()),
                ()->Assertions.assertEquals(categoryId, postCategoryOptional.get().getCategoryId()),
                ()->Assertions.assertEquals(postId,postCategoryOptional.get().getPostId())
        );
    }

    @Test
    @DisplayName("post-category 존재 여부-by-postCategoryId")
    @Order(4)
    void existsByPostCategoryId() {
        boolean actual = postCategoryRepository.existsByPostCategoryId(postCategoryId);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("post-category 존재 여부-by-postId + categoryId")
    @Order(5)
    void existsByPostIdAndCategoryId() {
        boolean actual = postCategoryRepository.existsByPostIdAndCategoryId(postId, categoryId);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("post-category 삭제-by-postCategoryId")
    @Order(6)
    void deleteByPostCategoryId() {
        postCategoryRepository.deleteByPostCategoryId(postCategoryId);
        boolean actual = postCategoryRepository.existsByPostCategoryId(postCategoryId);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("post-category 삭제-by-postId+categoryId")
    @Order(7)
    void deleteByPostIdAndCategoryId() {
        //포스트 - 카테고리 맵핑
        PostCategory postCategory = PostCategory.ofNewPostCategory(postId, categoryId);
        postCategoryRepository.save(postCategory);
        postCategoryId = postCategory.getPostCategoryId();
        postCategoryRepository.deleteByPostIdAndCategoryId(postId, categoryId);
        boolean actual = postCategoryRepository.existsByPostIdAndCategoryId(postId, categoryId);
        Assertions.assertFalse(actual);
    }

}