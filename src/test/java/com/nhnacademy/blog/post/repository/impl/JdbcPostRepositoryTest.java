package com.nhnacademy.blog.post.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.category.repository.impl.JdbcCategoryRepository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.pageable.PageRequest;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.member.repository.impl.JdbcMemberRepository;

import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.dto.PostResponse;
import com.nhnacademy.blog.post.dto.PostSearchParam;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;
import com.nhnacademy.blog.post.repository.PostRepository;
import com.nhnacademy.blog.postcategory.domain.PostCategory;
import com.nhnacademy.blog.postcategory.repository.PostCategoryRepository;
import com.nhnacademy.blog.postcategory.repository.impl.JdbcPostCategoryRepository;
import com.nhnacademy.blog.tag.domain.Tag;
import com.nhnacademy.blog.tag.repository.TagRepository;
import com.nhnacademy.blog.tag.repository.impl.JdbcTagRepository;
import com.nhnacademy.blog.tagging.domain.PostTag;
import com.nhnacademy.blog.tagging.repository.PostTagRepository;
import com.nhnacademy.blog.tagging.repository.impl.JdbcPostTagRepository;
import com.nhnacademy.blog.topic.domain.Topic;
import com.nhnacademy.blog.topic.repository.TopicRepository;
import com.nhnacademy.blog.topic.repository.impl.JdbcTopicRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.Optional;

@Slf4j
class JdbcPostRepositoryTest {

    static MemberRepository memberRepository;
    static PostRepository postRepository;
    static BlogRepository blogRepository;
    static CategoryRepository categoryRepository;
    static PostCategoryRepository postCategoryRepository;
    static TopicRepository topicRepository;
    static TagRepository tagRepository;
    static PostTagRepository postTagRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        memberRepository = (MemberRepository) context.getBean(JdbcMemberRepository.BEAN_NAME);
        postRepository = (PostRepository) context.getBean(JdbcPostRepository.BEAN_NAME);
        blogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
        categoryRepository = (CategoryRepository)context.getBean(JdbcCategoryRepository.BEAN_NAME);
        postCategoryRepository = (PostCategoryRepository) context.getBean(JdbcPostCategoryRepository.BEAN_NAME);
        topicRepository = (TopicRepository) context.getBean(JdbcTopicRepository.BEAN_NAME);
        tagRepository = (TagRepository) context.getBean(JdbcTagRepository.BEAN_NAME);
        postTagRepository = (PostTagRepository) context.getBean(JdbcPostTagRepository.BEAN_NAME);

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
    @DisplayName("글 등록")
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
    @DisplayName("글 등록 - 예외 - 존재하지 않는 블로그, 제약조건 위반")
    void save_exception_case1(){
        //given
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        Post post = Post.ofNewPost(Long.MAX_VALUE ,member.getMbNo(),"post-title","post-content",true);

        //then
        Assertions.assertThrows(DatabaseException.class,()->{
            postRepository.save(post);
        });
    }

    @Test
    @DisplayName("글 등록 - 예외 - 존재하지 않는 회원, 제약조건 위반")
    void save_exception_case2(){
        //given
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        Post post = Post.ofNewPost(blog.getBlogId() ,Long.MAX_VALUE,"post-title","post-content",true);

        //then
        Assertions.assertThrows(DatabaseException.class,()->{
            postRepository.save(post);
        });
    }

    @Test
    @DisplayName("글 수정")
    void update() {

        //given
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                post.getPostId(),
                blog.getBlogId(),
                "update-post-title",
                "update-post-content",
                false
        );
        postRepository.update(postUpdateRequest);

        Optional<Post> postOptional = postRepository.findByPostId(post.getPostId());

        //then
        Assertions.assertTrue(postOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(post.getPostId(), postOptional.get().getPostId()),
                ()->Assertions.assertEquals("update-post-title", postOptional.get().getPostTitle()),
                ()->Assertions.assertEquals("update-post-content", postOptional.get().getPostContent()),
                ()->Assertions.assertFalse(postOptional.get().isPostIsPublic()),
                ()->Assertions.assertNotNull(postOptional.get().getCreatedAt()),
                ()->Assertions.assertNotNull(postOptional.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("글 삭제")
    void deleteByPostId() {

        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);
        postRepository.deleteByPostId(post.getPostId());
        boolean actual = postRepository.existsByPostId(post.getPostId());

        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("글 공개 설정 : true")
    void updateByPostIdAndPostIsPublic_true() {
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);

        postRepository.updateByPostIdAndPostIsPublic(post.getPostId(),true);
        Optional<Post> postOptional = postRepository.findByPostId(post.getPostId());
        Assertions.assertTrue(postOptional.isPresent());
        Assertions.assertTrue(postOptional.get().isPostIsPublic());
    }

    @Test
    @DisplayName("글 공개 설정 : false")
    void updateByPostIdAndPostIsPublic_false() {
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);

        postRepository.updateByPostIdAndPostIsPublic(post.getPostId(),false);
        Optional<Post> postOptional = postRepository.findByPostId(post.getPostId());
        Assertions.assertTrue(postOptional.isPresent());
        Assertions.assertFalse(postOptional.get().isPostIsPublic());
    }

    @Test
    @DisplayName("글 존재 여부 체크")
    void existsByPostId() {
        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);

        boolean actual = postRepository.existsByPostId(post.getPostId());
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("글 조회")
    void findByPostId() {

        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Post post = Post.ofNewPost(blog.getBlogId(),member.getMbNo(),"post-title","post-content",true);
        postRepository.save(post);

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(
                post.getPostId(),
                blog.getBlogId(),
                "update-post-title",
                "update-post-content",
                false
        );
        postRepository.update(postUpdateRequest);

        Optional<Post> postOptional = postRepository.findByPostId(post.getPostId());

        Assertions.assertTrue(postOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertEquals(post.getPostId(), postOptional.get().getPostId()),
                ()->Assertions.assertEquals("update-post-title", postOptional.get().getPostTitle()),
                ()->Assertions.assertEquals("update-post-content", postOptional.get().getPostContent()),
                ()->Assertions.assertFalse(postOptional.get().isPostIsPublic()),
                ()->Assertions.assertNotNull(postOptional.get().getCreatedAt()),
                ()->Assertions.assertNotNull(postOptional.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("글 페이징 처리")
    void findAllByPageableAndPostSearchRequest() {

        Member member = Member.ofNewMember("marco@nhnacademy.com","마르코","12345","01012345678");
        memberRepository.save(member);

        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //topic-1 생성
        Topic topic1 = Topic.ofNewRootTopic("programming",1);
        topicRepository.save(topic1);
        //topic-2생성
        Topic topic2 = Topic.ofNewRootTopic("life",2);
        topicRepository.save(topic2);
        //카테고리-1생성
        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),topic1.getTopicId(),"java",1);
        categoryRepository.save(category1);
        //카테고리-2생성
        Category category2 = Category.ofNewRootCategory(blog.getBlogId(),topic2.getTopicId(),"hobby",2);
        categoryRepository.save(category2);

        //태그 등록
        Tag tag1 = Tag.ofNewTag("annotation");
        Tag tag2 = Tag.ofNewTag("@Component");
        tagRepository.save(tag1);
        tagRepository.save(tag2);

        for(int i=0; i<51; i++) {

            Post post = Post.ofNewPost(blog.getBlogId(), member.getMbNo(), "post-title-%d".formatted(i), "post-content-%d".formatted(i), true);
            postRepository.save(post);

            //post-category1 연결
            PostCategory postCategory1 = PostCategory.ofNewPostCategory(post.getPostId(),category1.getCategoryId());
            postCategoryRepository.save(postCategory1);

            //post-category2 연결
            PostCategory postCategory2 = PostCategory.ofNewPostCategory(post.getPostId(),category2.getCategoryId());
            postCategoryRepository.save(postCategory2);

            //post-tag1 연결
            PostTag postTag1 = PostTag.ofNewPostTag(post.getPostId(), tag1.getTagId());
            postTagRepository.save(postTag1);

            PostTag postTag2 = PostTag.ofNewPostTag(post.getPostId(), tag2.getTagId());
            postTagRepository.save(postTag2);
        }

        Pageable pageable = new PageRequest(0, 10);
        PostSearchParam postSearchParam = new PostSearchParam(blog.getBlogId(),null,true);
        Page<PostResponse> postResponsePage =  postRepository.findAllByPageableAndPostSearchRequest(pageable, postSearchParam);

        for(PostResponse postResponse : postResponsePage.getContent()){
            log.debug("postResponse: {}", postResponse);
        }

        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(10, postResponsePage.getContent().size());
                },
                ()->{
                    Assertions.assertEquals(51,postResponsePage.getTotalElements());
                },
                ()->{
                    Assertions.assertEquals(6,postResponsePage.getTotalPages());
                }
        );
    }

}