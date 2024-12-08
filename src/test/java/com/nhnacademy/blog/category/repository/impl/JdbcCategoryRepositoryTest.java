package com.nhnacademy.blog.category.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequestDto;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.role.repository.RoleRepository;
import com.nhnacademy.blog.role.repository.impl.JdbcRoleRepository;
import org.junit.jupiter.api.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcCategoryRepositoryTest {
    static CategoryRepository jdbcCategoryRepository;
    static BlogRepository jdbcBlogRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        jdbcBlogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
        jdbcCategoryRepository = (CategoryRepository) context.getBean(JdbcCategoryRepository.BEAN_NAME);
    }

    @BeforeEach
    void setUp() {
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("카테고리 등록")
    void save() {

        //given
        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        //when
        jdbcCategoryRepository.save(category);

        //then
        Optional<Category> categoryOptional = jdbcCategoryRepository.findByCategoryId(category.getCategoryId());

        Assertions.assertAll(
                ()->Assertions.assertNotNull(category.getCategoryId()),
                ()->Assertions.assertEquals(category.getCategoryName(),categoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(category.getCategorySec(),categoryOptional.get().getCategorySec()),
                ()->Assertions.assertNotNull(categoryOptional.get().getCreatedAt())
        );

    }

    @Test
    @DisplayName("서브카테고리 등록")
    void save_subCategory(){

        //given
        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        //when
        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        jdbcCategoryRepository.save(category1);
        Category category2 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"스프링-코어",1);
        jdbcCategoryRepository.save(category2);

        Optional<Category> categoryOptional = jdbcCategoryRepository.findByCategoryId(category2.getCategoryId());

        //then
        Assertions.assertAll(
                ()->Assertions.assertNotNull(category2.getCategoryId()),
                ()->Assertions.assertEquals(category1.getCategoryId(),categoryOptional.get().getCategoryPid()),
                ()->Assertions.assertEquals(category2.getCategoryName(),categoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(category2.getCategorySec(),categoryOptional.get().getCategorySec()),
                ()->Assertions.assertNotNull(category2.getCreatedAt())
        );

    }

    @Test
    @DisplayName("category-업데이트")
    void update() {

        //given
        Blog blog = Blog.ofNewBlog("NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        jdbcBlogRepository.save(blog);

        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        jdbcCategoryRepository.save(category1);

        Category category2 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"스프링-코어",1);
        jdbcCategoryRepository.save(category2);

        //when
        CategoryUpdateRequestDto categoryUpdateRequestDto = new CategoryUpdateRequestDto(category2.getCategoryId(),category2.getCategoryPid(),null,"Spring-core",10);
        jdbcCategoryRepository.update(categoryUpdateRequestDto);

        //then
        Optional<Category> categoryOptional = jdbcCategoryRepository.findByCategoryId(category2.getCategoryId());
        Assertions.assertAll(
                ()->Assertions.assertEquals(category2.getCategoryId(),categoryOptional.get().getCategoryId()),
                ()->Assertions.assertEquals(categoryUpdateRequestDto.getCategoryPid(),categoryOptional.get().getCategoryPid()),
                ()->Assertions.assertEquals(categoryUpdateRequestDto.getCategoryName(),categoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(categoryUpdateRequestDto.getCategorySec(),categoryOptional.get().getCategorySec()),
                ()->Assertions.assertNotNull(categoryOptional.get().getUpdatedAt())
        );
    }

    @Test
    void delete() {

    }

    @Test
    void findByCategoryId() {
    }

    @Test
    void findAllByPageRequest() {
    }

    @Test
    void count() {
    }

    @Test
    void existsByCategoryId() {
    }

}