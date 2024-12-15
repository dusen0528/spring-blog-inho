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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JdbcCategoryRepositoryTest {
    static CategoryRepository categoryRepository;
    static BlogRepository blogRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        blogRepository = (BlogRepository) context.getBean(JdbcBlogRepository.BEAN_NAME);
        categoryRepository = (CategoryRepository) context.getBean(JdbcCategoryRepository.BEAN_NAME);
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
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        //when
        categoryRepository.save(category);

        //then
        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category.getCategoryId());
        Assertions.assertTrue(categoryOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(category.getCategoryId()),
                ()->Assertions.assertNull(categoryOptional.get().getCategoryPid()),
                ()->Assertions.assertEquals(category.getCategoryName(),categoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(category.getCategorySec(),categoryOptional.get().getCategorySec()),
                ()->Assertions.assertNotNull(categoryOptional.get().getCreatedAt())
        );

    }

    @Test
    @DisplayName("서브카테고리 등록")
    void save_subCategory(){

        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        //when
        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        categoryRepository.save(category1);
        Category category2 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"스프링-코어",1);
        categoryRepository.save(category2);

        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category2.getCategoryId());

        //then
        Assertions.assertTrue(categoryOptional.isPresent());
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
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        categoryRepository.save(category1);

        Category category2 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"스프링-코어",1);
        categoryRepository.save(category2);

        //when
        CategoryUpdateRequestDto categoryUpdateRequestDto = new CategoryUpdateRequestDto(category2.getCategoryId(),category2.getCategoryPid(),null,"Spring-core",10);
        categoryRepository.update(categoryUpdateRequestDto);

        //then
        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category2.getCategoryId());
        Assertions.assertAll(
                ()->Assertions.assertEquals(category2.getCategoryId(),categoryOptional.get().getCategoryId()),
                ()->Assertions.assertEquals(categoryUpdateRequestDto.getCategoryPid(),categoryOptional.get().getCategoryPid()),
                ()->Assertions.assertEquals(categoryUpdateRequestDto.getCategoryName(),categoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(categoryUpdateRequestDto.getCategorySec(),categoryOptional.get().getCategorySec()),
                ()->Assertions.assertNotNull(categoryOptional.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("카테고리-삭제")
    void delete() {
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);
        //when
        blogRepository.deleteByBlogId(blog.getBlogId());

        boolean actual = blogRepository.existByBlogId(blog.getBlogId());

        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("category 조회")
    void findByCategoryId() {
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        categoryRepository.save(category);

        //when
        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category.getCategoryId());

        //then
        Assertions.assertTrue(categoryOptional.isPresent());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(category.getCategoryId()),
                ()->Assertions.assertNull(categoryOptional.get().getCategoryPid()),
                ()->Assertions.assertEquals(category.getCategoryName(),categoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(category.getCategorySec(),categoryOptional.get().getCategorySec()),
                ()->Assertions.assertNotNull(categoryOptional.get().getCreatedAt())
        );

    }

    @Test
    @DisplayName("category 전체조회")
    void findAll() {
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-1",1);
        Category category2 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-2",2);
        Category category3 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-3",3);
        Category category4 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-4",4);
        Category category5 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-5",5);

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);

        List<Category> categoryList = categoryRepository.findAll(blog.getBlogId(), null);

        log.debug("categoryList-size: {}", categoryList.size());

        Assertions.assertAll(
                ()->Assertions.assertEquals(5, categoryList.size()),
                ()->Assertions.assertEquals(category1.getCategoryId(),  categoryList.get(0).getCategoryId()),
                ()->Assertions.assertEquals(category2.getCategoryId(),  categoryList.get(1).getCategoryId()),
                ()->Assertions.assertEquals(category3.getCategoryId(),  categoryList.get(2).getCategoryId()),
                ()->Assertions.assertEquals(category4.getCategoryId(),  categoryList.get(3).getCategoryId()),
                ()->Assertions.assertEquals(category5.getCategoryId(),  categoryList.get(4).getCategoryId())
        );
    }

    @Test
    @DisplayName("category 서브 카테고리 - 조회")
    void findAll_subCategory() {
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Category category1 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-1",1);
        Category category2 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-2",2);
        Category category3 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-3",3);
        Category category4 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-4",4);
        Category category5 = Category.ofNewRootCategory(blog.getBlogId(),null,"카테고리-5",5);

        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        categoryRepository.save(category4);
        categoryRepository.save(category5);

        Category subCategory1 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"카테고리-1-1",1);
        Category subCategory2 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"카테고리-1-2",1);
        Category subCategory3 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"카테고리-1-3",1);
        Category subCategory4 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"카테고리-1-4",1);
        Category subCategory5 = Category.ofNewSubCategory(category1.getCategoryId(), blog.getBlogId(),null,"카테고리-1-5",1);

        categoryRepository.save(subCategory1);
        categoryRepository.save(subCategory2);
        categoryRepository.save(subCategory3);
        categoryRepository.save(subCategory4);
        categoryRepository.save(subCategory5);

        List<Category> categoryList = categoryRepository.findAll(blog.getBlogId(), category1.getCategoryId());

        log.debug("categoryList-size: {}", categoryList.size());

        Assertions.assertAll(
                ()->Assertions.assertEquals(5, categoryList.size()),
                ()->Assertions.assertEquals(subCategory1.getCategoryId(),  categoryList.get(0).getCategoryId()),
                ()->Assertions.assertEquals(subCategory2.getCategoryId(),  categoryList.get(1).getCategoryId()),
                ()->Assertions.assertEquals(subCategory3.getCategoryId(),  categoryList.get(2).getCategoryId()),
                ()->Assertions.assertEquals(subCategory4.getCategoryId(),  categoryList.get(3).getCategoryId()),
                ()->Assertions.assertEquals(subCategory5.getCategoryId(),  categoryList.get(4).getCategoryId())
        );
    }

    @Test
    @DisplayName("categoryId 존재여부 체크")
    void existsByCategoryId() {
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.save(blog);

        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        //when
        categoryRepository.save(category);

        //then
        Assertions.assertTrue(categoryRepository.existsByCategoryId(category.getCategoryId()));
    }

}