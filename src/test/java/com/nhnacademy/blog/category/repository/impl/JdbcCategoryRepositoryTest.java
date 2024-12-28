package com.nhnacademy.blog.category.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.JpaBlogRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.repository.JpaCategoryRepository;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class})
class JdbcCategoryRepositoryTest {
    @Autowired
    JpaCategoryRepository categoryRepository;
    @Autowired
    JpaBlogRepository blogRepository;

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
        Optional<Category> categoryOptional = categoryRepository.findById(category.getCategoryId());
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

        Optional<Category> categoryOptional = categoryRepository.findById(category2.getCategoryId());

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
        long categoryId = category2.getCategoryId();

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        categoryOptional.ifPresent(category -> {
            category.update(null,null,"Spring-core",10);
            categoryRepository.saveAndFlush(category);
        });

        //then
        Optional<Category> updatedCategoryOptional = categoryRepository.findById(categoryId);
        Assertions.assertTrue(updatedCategoryOptional.isPresent());

        Assertions.assertAll(
                ()->Assertions.assertEquals(categoryId,updatedCategoryOptional.get().getCategoryId()),
                ()->Assertions.assertEquals("Spring-core",updatedCategoryOptional.get().getCategoryName()),
                ()->Assertions.assertEquals(10,updatedCategoryOptional.get().getCategorySec())
        );
    }

    @Test
    @DisplayName("카테고리-삭제")
    void delete() {
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.saveAndFlush(blog);
        long blogId = blog.getBlogId();
        //when
        blogRepository.deleteById(blogId);
        blogRepository.flush();

        boolean actual = blogRepository.existsById(blogId);

        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("category 조회")
    void findByCategoryId() {
        //given
        Blog blog = Blog.ofNewBlog("marco",true,"NHN아카데미-blog","nhn-academy-marco","NHN아카데미-블로그 입니다.");
        blogRepository.saveAndFlush(blog);

        Category category = Category.ofNewRootCategory(blog.getBlogId(),null,"스프링",1);
        categoryRepository.saveAndFlush(category);
        long categoryId = category.getCategoryId();
        //when
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

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
        blogRepository.saveAndFlush(blog);

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

        List<Category> categoryList = categoryRepository.findAllByBlogId(blog.getBlogId());

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

        List<Category> categoryList = categoryRepository.findAllByBlogIdAndCategoryPid(blog.getBlogId(), category1.getCategoryId());

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
        Assertions.assertTrue(categoryRepository.existsById(category.getCategoryId()));
    }

    @Test
    @DisplayName("서브카테고리 존재유무 : true")
    void existsSubCategoryByCategoryId(){

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

        boolean actual = categoryRepository.existsByCategoryPid(category1.getCategoryId());
        Assertions.assertTrue(actual);

    }

    @Test
    @DisplayName("서브카테고리 존재유무 : false")
    void notExistsSubCategoryByCategoryId(){
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

        boolean actual = categoryRepository.existsByCategoryPid(category2.getCategoryId());
        Assertions.assertFalse(actual);
    }
}