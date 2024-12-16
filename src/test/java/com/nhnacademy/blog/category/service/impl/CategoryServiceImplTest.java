package com.nhnacademy.blog.category.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryResponse;
import com.nhnacademy.blog.category.dto.RootCategoryCreateRequest;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.category.service.CategoryService;
import com.nhnacademy.blog.common.context.ApplicationContext;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.exception.CommonHttpException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.topic.repository.TopicRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
class CategoryServiceImplTest {

    CategoryService categoryService;
    CategoryRepository categoryRepository;
    BlogRepository blogRepository;
    TopicRepository topicRepository;
    BlogMemberMappingRepository blogMemberMappingRepository;


    @BeforeEach
    void setUp() {
        MemberThreadLocal.setMemberNo(1L);
        categoryRepository = Mockito.mock(CategoryRepository.class);
        blogRepository = Mockito.mock(BlogRepository.class);
        topicRepository = Mockito.mock(TopicRepository.class);
        blogMemberMappingRepository = Mockito.mock(BlogMemberMappingRepository.class);

        categoryService = new CategoryServiceImpl(categoryRepository, blogRepository, topicRepository, blogMemberMappingRepository);
    }

    @AfterEach
    void tearDown() {
        MemberThreadLocal.removeMemberNo();
    }

    @Test
    @DisplayName("Root Category 생성")
    void createRootCategory() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        Mockito.doAnswer(invocationOnMock -> {
            Category paramCategory = invocationOnMock.getArgument(0);
            //save()호출시 categoryId를 1로 변경
            Field field = paramCategory.getClass().getDeclaredField("categoryId");
            field.setAccessible(true);
            field.set(paramCategory, 1L);
            log.debug("paramCategory: {}", paramCategory);
            return null;
        }).when(categoryRepository).save(Mockito.any(Category.class));

        Category category = Category.ofExistingCategory(1L,2L,1L,1,"it/internet",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));
        RootCategoryCreateRequest rootCategoryCreateRequest = new RootCategoryCreateRequest(1L,1,"java",10);

        CategoryResponse categoryResponse = categoryService.createRootCategory(rootCategoryCreateRequest);

        Assertions.assertAll(
                ()->Assertions.assertEquals(category.getCategoryId(),categoryResponse.getCategoryId()),
                ()->Assertions.assertEquals(category.getCategoryPid(),categoryResponse.getCategoryPid()),
                ()->Assertions.assertEquals(category.getCategoryName(),categoryResponse.getCategoryName()),
                ()->Assertions.assertEquals(category.getCategorySec(),categoryResponse.getCategorySec()),
                ()->Assertions.assertEquals(category.getTopicId(),categoryResponse.getTopicId()),
                ()->Assertions.assertEquals(category.getBlogId(),categoryResponse.getBlogId())
        );

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(topicRepository,Mockito.times(1)).existByTopicId(Mockito.anyInt());
        Mockito.verify(categoryRepository,Mockito.times(1)).findByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).save(Mockito.any(Category.class));

    }

    @Test
    @DisplayName("Root Category 생성 예외 : 카테고리 생성시 해당 회원이 블로그의 OWNER가 아니면, 403 , forbidden exception")
    void createRootCategory_exception_case1() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.empty());

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        RootCategoryCreateRequest rootCategoryCreateRequest = new RootCategoryCreateRequest(1L,1,"java",10);

        CommonHttpException commonHttpException = Assertions.assertThrows(ForbiddenException.class,()->{
            categoryService.createRootCategory(rootCategoryCreateRequest);
        });

        log.debug("error:{}, code:{}",commonHttpException.getMessage(),commonHttpException.getStatusCode(),commonHttpException);

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));

    }

    @Test
    void createSubCategory() {
        Context context = ContextHolder.getApplicationContext();

    }

    @Test
    void updateRootCategory() {

    }

    @Test
    void updateSubCategory() {

    }

    @Test
    void deleteCategory() {

    }

    @Test
    void getAllCategories() {

    }
}