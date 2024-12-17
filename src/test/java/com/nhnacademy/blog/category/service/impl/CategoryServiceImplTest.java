package com.nhnacademy.blog.category.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.*;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.category.service.CategoryService;
import com.nhnacademy.blog.common.context.ApplicationContext;
import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.exception.*;
import com.nhnacademy.blog.topic.repository.TopicRepository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

        Category category = Category.ofExistingCategory(1L,null,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        RootCategoryCreateRequest rootCategoryCreateRequest = new RootCategoryCreateRequest(1L,1,"java",10);
        CategoryResponse categoryResponse = categoryService.createRootCategory(rootCategoryCreateRequest);
        log.debug("category:{}",category);
        log.debug("categoryResponse: {}", categoryResponse);

        Assertions.assertAll(
                ()->Assertions.assertEquals(1L,categoryResponse.getCategoryId()),
                ()->Assertions.assertEquals(null,categoryResponse.getCategoryPid()),
                ()->Assertions.assertEquals("java",categoryResponse.getCategoryName()),
                ()->Assertions.assertEquals(10,categoryResponse.getCategorySec()),
                ()->Assertions.assertEquals(1,categoryResponse.getTopicId()),
                ()->Assertions.assertEquals(1L,categoryResponse.getBlogId())
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

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_MEMBER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

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
    @DisplayName("Root Category 생성 예외 : 카테고리 생성시 블로그가 존재하지 않을 때")
    void createRootCategory_exception_case3() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        RootCategoryCreateRequest rootCategoryCreateRequest = new RootCategoryCreateRequest(1L,1,"java",10);
        Assertions.assertThrows(BadRequestException.class,()->{
            categoryService.createRootCategory(rootCategoryCreateRequest);
        });

        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));
    }

    @Test
    @DisplayName("Root Category 생성 예외 : 카테고리 생성시 Topic이 존재하지 않을 때")
    void createRootCategory_exception_case4() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(false);

        RootCategoryCreateRequest rootCategoryCreateRequest = new RootCategoryCreateRequest(1L,1,"java",10);

        Assertions.assertThrows(BadRequestException.class,()->{
            categoryService.createRootCategory(rootCategoryCreateRequest);
        });

        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));
        Mockito.verify(topicRepository,Mockito.times(1)).existByTopicId(Mockito.anyInt());
    }

    @Test
    @DisplayName("subCategory 생성")
    void createSubCategory() {
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");

        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
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

        Category category = Category.ofExistingCategory(1L,10L,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        SubCategoryCreateRequest subCategoryCreateRequest = new SubCategoryCreateRequest(1L,10L,1L,1,"java",10);
        CategoryResponse categoryResponse = categoryService.createSubCategory(subCategoryCreateRequest);

        Assertions.assertAll(
                ()->Assertions.assertEquals(1L,categoryResponse.getCategoryId()),
                ()->Assertions.assertEquals(10L,categoryResponse.getCategoryPid()),
                ()->Assertions.assertEquals("java",categoryResponse.getCategoryName()),
                ()->Assertions.assertEquals(10,categoryResponse.getCategorySec()),
                ()->Assertions.assertEquals(1,categoryResponse.getTopicId()),
                ()->Assertions.assertEquals(1L,categoryResponse.getBlogId())
        );

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(topicRepository,Mockito.times(1)).existByTopicId(Mockito.anyInt());
        Mockito.verify(categoryRepository,Mockito.times(1)).findByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).save(Mockito.any(Category.class));
    }

    @Test
    @DisplayName("subCategory 생성 예외 : 카테고리 생성시 해당 회원이 블로그의 OWNER가 아니면, 403 , forbidden exception")
    void createSubCategory_exception_case1() {
        //given
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_MEMBER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);
        SubCategoryCreateRequest subCategoryCreateRequest = new SubCategoryCreateRequest(1L,10L,1L,1,"java",10);
        //when
        Assertions.assertThrows(ForbiddenException.class,()->{
            categoryService.createSubCategory(subCategoryCreateRequest);
        });
        //then
        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));
    }


    @Test
    @DisplayName("subCategory 생성 예외 : 카테고리 생성시 부모 카테고리가 존재하지 않을 때")
    void createSubCategory_exception_case2() {
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");

        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        Category category = Category.ofExistingCategory(1L,10L,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        SubCategoryCreateRequest subCategoryCreateRequest = new SubCategoryCreateRequest(1L,10L,1L,1,"java",10);
        Assertions.assertThrows(NotFoundException.class,()->{
            categoryService.createSubCategory(subCategoryCreateRequest);
        });

        Mockito.verify(categoryRepository,Mockito.times(1)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));

    }

    @Test
    @DisplayName("subCategory 생성 예외 : 카테고리 생성시 블로그가 존재하지 않을 때")
    void createSubCategory_exception_case3() {
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");

        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        Category category = Category.ofExistingCategory(1L,10L,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        SubCategoryCreateRequest subCategoryCreateRequest = new SubCategoryCreateRequest(1L,10L,1L,1,"java",10);
        Assertions.assertThrows(BadRequestException.class,()->{
            categoryService.createSubCategory(subCategoryCreateRequest);
        });

        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));

    }

    @Test
    @DisplayName("subCategory 생성 예외 : 카테고리 생성시 topic 존재하지 않을 때")
    void createSubCategory_exception_case4() {
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");

        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(false);

        Category category = Category.ofExistingCategory(1L,10L,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        SubCategoryCreateRequest subCategoryCreateRequest = new SubCategoryCreateRequest(1L,10L,1L,1,"java",10);
        Assertions.assertThrows(BadRequestException.class,()->{
            categoryService.createSubCategory(subCategoryCreateRequest);
        });

        Mockito.verify(topicRepository,Mockito.times(1)).existByTopicId(Mockito.anyInt());
        Mockito.verify(categoryRepository,Mockito.never()).save(Mockito.any(Category.class));

    }

    @Test
    @DisplayName("rootCategory 수정")
    void updateRootCategory() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        Category category = Category.ofExistingCategory(1L,null,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        RootCategoryUpdateRequest rootCategoryUpdateRequest = new RootCategoryUpdateRequest(1L,1L,1,"java",10);
        CategoryResponse categoryResponse = categoryService.updateRootCategory(rootCategoryUpdateRequest);

        Assertions.assertAll(
                ()->Assertions.assertEquals(1L,categoryResponse.getCategoryId()),
                ()->Assertions.assertEquals(null,categoryResponse.getCategoryPid()),
                ()->Assertions.assertEquals("java",categoryResponse.getCategoryName()),
                ()->Assertions.assertEquals(10,categoryResponse.getCategorySec()),
                ()->Assertions.assertEquals(1,categoryResponse.getTopicId()),
                ()->Assertions.assertEquals(1L,categoryResponse.getBlogId())
        );

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(topicRepository,Mockito.times(1)).existByTopicId(Mockito.anyInt());
        Mockito.verify(categoryRepository,Mockito.times(1)).findByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).update(Mockito.any());

    }

    @Test
    @DisplayName("rootCategory 수정 : 권한 없음")
    void updateRootCategory_exception_case1() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L, 1L, 1L, "ROLE_MEMBER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        Category category = Category.ofExistingCategory(1L, null, 1L, 1, "java", 10, LocalDateTime.now().minusDays(10), LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        RootCategoryUpdateRequest rootCategoryUpdateRequest = new RootCategoryUpdateRequest(1L, 1L, 1, "java", 10);
        Assertions.assertThrows(ForbiddenException.class, () -> {
            categoryService.updateRootCategory(rootCategoryUpdateRequest);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("rootCategory 수정 - 카테고리가 존재하지 않음 ")
    void updateRootCategory_exception_case2() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        RootCategoryUpdateRequest rootCategoryUpdateRequest = new RootCategoryUpdateRequest(1L,1L,1,"java",10);
        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.updateRootCategory(rootCategoryUpdateRequest);
        });

        Mockito.verify(categoryRepository, Mockito.times(1)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("rootCategory 수정 - 해당 블로그가 존재하지 않음, 잘못된 요청 400")
    void updateRootCategory_exception_case3() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        RootCategoryUpdateRequest rootCategoryUpdateRequest = new RootCategoryUpdateRequest(1L,1L,1,"java",10);
        Assertions.assertThrows(BadRequestException.class, () -> {
            categoryService.updateRootCategory(rootCategoryUpdateRequest);
        });

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("rootCategory 수정 - topic이 존재하지 않음, 잘못된 요청 400")
    void updateRootCategory_exception_case4() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(false);

        RootCategoryUpdateRequest rootCategoryUpdateRequest = new RootCategoryUpdateRequest(1L,1L,1,"java",10);
        Assertions.assertThrows(BadRequestException.class, () -> {
            categoryService.updateRootCategory(rootCategoryUpdateRequest);
        });

        Mockito.verify(topicRepository, Mockito.times(1)).existByTopicId(Mockito.anyInt());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("서브카테고리 수정")
    void updateSubCategory() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        Category category = Category.ofExistingCategory(1L,10L,1L,1,"java",10, LocalDateTime.now().minusDays(10),LocalDateTime.now());
        Mockito.when(categoryRepository.findByCategoryId(Mockito.anyLong())).thenReturn(Optional.of(category));

        SubCategoryUpdateRequest subCategoryUpdateRequest = new SubCategoryUpdateRequest(1L,10l,1L,1,"java",10);
        CategoryResponse categoryResponse = categoryService.updateSubCategory(subCategoryUpdateRequest);

        Assertions.assertAll(
                ()->Assertions.assertEquals(1L,categoryResponse.getCategoryId()),
                ()->Assertions.assertEquals(10L,categoryResponse.getCategoryPid()),
                ()->Assertions.assertEquals("java",categoryResponse.getCategoryName()),
                ()->Assertions.assertEquals(10,categoryResponse.getCategorySec()),
                ()->Assertions.assertEquals(1,categoryResponse.getTopicId()),
                ()->Assertions.assertEquals(1L,categoryResponse.getBlogId())
        );

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(categoryRepository, Mockito.times(2)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(topicRepository,Mockito.times(1)).existByTopicId(Mockito.anyInt());
        Mockito.verify(categoryRepository,Mockito.times(1)).findByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).update(Mockito.any());

    }

    @Test
    @DisplayName("서브카테고리 수정 - 예외 : 권한 없음, 403")
    void updateSubCategory_exception_case1() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_MEMBER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        SubCategoryUpdateRequest subCategoryUpdateRequest = new SubCategoryUpdateRequest(1L,10l,1L,1,"java",10);

        Assertions.assertThrows(ForbiddenException.class, () -> {
            categoryService.updateSubCategory(subCategoryUpdateRequest);
        });

        Mockito.verify(blogMemberMappingRepository, Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("서브카테고리 수정 - 예외 : 400, 존재하지 않은 카테고리")
    void updateSubCategory_exception_case2() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        SubCategoryUpdateRequest subCategoryUpdateRequest = new SubCategoryUpdateRequest(1L,10l,1L,1,"java",10);

        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.updateSubCategory(subCategoryUpdateRequest);
        });

        Mockito.verify(categoryRepository, Mockito.atLeast(1)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("서브카테고리 수정 - 예외 : 400, 존재하지 않은 blog")
    void updateSubCategory_exception_case3() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(true);

        SubCategoryUpdateRequest subCategoryUpdateRequest = new SubCategoryUpdateRequest(1L,10l,1L,1,"java",10);

        Assertions.assertThrows(BadRequestException.class, () -> {
            categoryService.updateSubCategory(subCategoryUpdateRequest);
        });

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("서브카테고리 수정 - 예외 : 400, 존재하지 않은 topic")
    void updateSubCategory_exception_case4() {

        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(topicRepository.existByTopicId(Mockito.anyInt())).thenReturn(false);

        SubCategoryUpdateRequest subCategoryUpdateRequest = new SubCategoryUpdateRequest(1L,10l,1L,1,"java",10);

        Assertions.assertThrows(BadRequestException.class, () -> {
            categoryService.updateSubCategory(subCategoryUpdateRequest);
        });

        Mockito.verify(blogRepository, Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).update(Mockito.any());

    }

    @Test
    @DisplayName("카테고리 삭제")
    void deleteCategory() {
    //given
        //0.삭제 권한체크
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        //1.카테고리 존재여부체크
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);

        //2.서브카테고리 존재여부 체크
        Mockito.when(categoryRepository.existsSubCategoryByCategoryId(Mockito.anyLong())).thenReturn(false);

        //3.블로그 존재여부 체크
        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);

    //when
        CategoryDeleteRequest categoryDeleteRequest = new CategoryDeleteRequest(1L,1L);
        categoryService.deleteCategory(categoryDeleteRequest);
    //then
        Mockito.verify(blogMemberMappingRepository,Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.times(1)).deleteByCategoryId(Mockito.anyLong());
    }

    @Test
    @DisplayName("카테고리 삭제 - 예외 : 권한 없음")
    void deleteCategory_exception_case1() {

        //0.삭제 권한체크
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_MEMBER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        //1.카테고리 존재여부체크
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);

        //2.서브카테고리 존재여부 체크
        Mockito.when(categoryRepository.existsSubCategoryByCategoryId(Mockito.anyLong())).thenReturn(false);


        CategoryDeleteRequest categoryDeleteRequest = new CategoryDeleteRequest(1L,1L);
        Assertions.assertThrows(ForbiddenException.class, () -> {
            categoryService.deleteCategory(categoryDeleteRequest);
        });

        Mockito.verify(blogMemberMappingRepository,Mockito.times(1)).findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong());;
        Mockito.verify(categoryRepository,Mockito.never()).deleteByCategoryId(Mockito.anyLong());
    }

    @Test
    @DisplayName("카테고리 삭제 - 예외 : category 존재하지 않음, 404 ")
    void deleteCategory_exception_case2() {

        //0.삭제 권한체크
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        //1.카테고리 존재여부체크
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(false);

        //2.서브카테고리 존재여부 체크
        Mockito.when(categoryRepository.existsSubCategoryByCategoryId(Mockito.anyLong())).thenReturn(false);

        CategoryDeleteRequest categoryDeleteRequest = new CategoryDeleteRequest(1L,1L);
        Assertions.assertThrows(NotFoundException.class, () -> {
            categoryService.deleteCategory(categoryDeleteRequest);
        });

        Mockito.verify(categoryRepository,Mockito.times(1)).existsByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).deleteByCategoryId(Mockito.anyLong());
    }

    @Test
    @DisplayName("카테고리 삭제 - 예외 : subCategory가 존재 한다면 409 ")
    void deleteCategory_exception_case3() {

        //0.삭제 권한체크
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(1L,1L,1L,"ROLE_OWNER");
        Mockito.when(blogMemberMappingRepository.findByMbNoAndBlogId(Mockito.anyLong(),Mockito.anyLong())).thenReturn(Optional.of(blogMemberMapping));

        //1.카테고리 존재여부체크
        Mockito.when(categoryRepository.existsByCategoryId(Mockito.anyLong())).thenReturn(true);

        //2.서브카테고리 존재여부 체크
        Mockito.when(categoryRepository.existsSubCategoryByCategoryId(Mockito.anyLong())).thenReturn(true);

        CategoryDeleteRequest categoryDeleteRequest = new CategoryDeleteRequest(1L,1L);
        Assertions.assertThrows(ConflictException.class, () -> {
            categoryService.deleteCategory(categoryDeleteRequest);
        });

        Mockito.verify(categoryRepository,Mockito.times(1)).existsSubCategoryByCategoryId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).deleteByCategoryId(Mockito.anyLong());
    }

    @Test
    @DisplayName("계층적 카테고리 리스트 : 1뎁스")
    void getAllCategories_depth1() {

        List<CategoryResponse> dbCategoryList = new ArrayList<>(){{
            add(new CategoryResponse(1l, null, 1l, 1, "root-1", 1));
            add(new CategoryResponse(2l, null, 1l, 1, "root-2", 2));
            add(new CategoryResponse(3l, null, 1l, 1, "root-3", 3));
            add(new CategoryResponse(4l, null, 1l, 1, "root-4", 4));
            add(new CategoryResponse(5l, null, 1l, 1, "root-5", 5));
        }};

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(categoryRepository.findAllByBlogId(Mockito.anyLong())).thenReturn(dbCategoryList);

        List<CategoryResponse> categoryResponseList = categoryService.getAllCategories(1l);

        for(CategoryResponse categoryResponse : categoryResponseList) {
            log.debug("result: {}", categoryResponse);
        }
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(5,categoryResponseList.size());
                },
                ()->{
                    Assertions.assertEquals(1,categoryResponseList.get(0).getCategorySec());
                    Assertions.assertEquals(2,categoryResponseList.get(1).getCategorySec());
                    Assertions.assertEquals(3,categoryResponseList.get(2).getCategorySec());
                    Assertions.assertEquals(4,categoryResponseList.get(3).getCategorySec());
                    Assertions.assertEquals(5,categoryResponseList.get(4).getCategorySec());
                }
        );

        Mockito.verify(categoryRepository,Mockito.times(1)).findAllByBlogId(Mockito.anyLong());

    }

    @Test
    @DisplayName("계층적 카테고리 리스트 : 2뎁스")
    void getAllCategories_depth2() {

        List<CategoryResponse> dbCategoryList = new ArrayList<>(){{
            add(new CategoryResponse(1l, null, 1l, 1, "root-1", 1));
            add(new CategoryResponse(2l, null, 1l, 1, "root-2", 2));
            add(new CategoryResponse(3l, null, 1l, 1, "root-3", 3));
            add(new CategoryResponse(4l, null, 1l, 1, "root-4", 4));
            add(new CategoryResponse(5l, null, 1l, 1, "root-5", 5));

            add(new CategoryResponse(6l, 1l, 1l, 1, "root-1-4", 4));
            add(new CategoryResponse(7l, 1l, 1l, 1, "root-1-3", 3));
            add(new CategoryResponse(8l, 1l, 1l, 1, "root-1-2", 2));
            add(new CategoryResponse(9l, 1l, 1l, 1, "root-1-1", 1));

        }};

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(categoryRepository.findAllByBlogId(Mockito.anyLong())).thenReturn(dbCategoryList);

        List<CategoryResponse> categoryResponseList = categoryService.getAllCategories(1l);

        for(CategoryResponse categoryResponse : categoryResponseList) {
            log.debug("result: {}", categoryResponse);
        }
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(5,categoryResponseList.size());
                },
                ()->{
                    Assertions.assertEquals(4,categoryResponseList.get(0).getSubCategories().size());
                },
                ()->{
                    Assertions.assertEquals(1,categoryResponseList.get(0).getSubCategories().get(0).getCategorySec());
                    Assertions.assertEquals(2,categoryResponseList.get(0).getSubCategories().get(1).getCategorySec());
                    Assertions.assertEquals(3,categoryResponseList.get(0).getSubCategories().get(2).getCategorySec());
                    Assertions.assertEquals(4,categoryResponseList.get(0).getSubCategories().get(3).getCategorySec());
                }
        );

        Mockito.verify(categoryRepository,Mockito.times(1)).findAllByBlogId(Mockito.anyLong());

    }

    @Test
    @DisplayName("계층적 카테고리 리스트 : 3뎁스")
    void getAllCategories_depth3() {

        List<CategoryResponse> dbCategoryList = new ArrayList<>(){{
            add(new CategoryResponse(1l, null, 1l, 1, "root-1", 1));
            add(new CategoryResponse(2l, null, 1l, 1, "root-2", 2));
            add(new CategoryResponse(3l, null, 1l, 1, "root-3", 3));
            add(new CategoryResponse(4l, null, 1l, 1, "root-4", 4));
            add(new CategoryResponse(5l, null, 1l, 1, "root-5", 5));

            add(new CategoryResponse(6l, 1l, 1l, 1, "root-1-4", 4));
            add(new CategoryResponse(7l, 1l, 1l, 1, "root-1-3", 3));
            add(new CategoryResponse(8l, 1l, 1l, 1, "root-1-2", 2));
            add(new CategoryResponse(9l, 1l, 1l, 1, "root-1-1", 1));

            add(new CategoryResponse(10l, 9l, 1l, 1, "root-1-1-1", 1));
            add(new CategoryResponse(11l, 9l, 1l, 1, "root-1-1-2", 2));
            add(new CategoryResponse(12l, 9l, 1l, 1, "root-1-1-3", 3));


        }};

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(true);
        Mockito.when(categoryRepository.findAllByBlogId(Mockito.anyLong())).thenReturn(dbCategoryList);

        List<CategoryResponse> categoryResponseList = categoryService.getAllCategories(1l);

        for(CategoryResponse categoryResponse : categoryResponseList) {
            log.debug("result: {}", categoryResponse);
        }
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(5,categoryResponseList.size());
                },
                ()->{
                    Assertions.assertEquals(4,categoryResponseList.get(0).getSubCategories().size());
                },
                ()->{
                    //1-1-1 ~ 1-1-3
                    Assertions.assertEquals(3,categoryResponseList.get(0).getSubCategories().get(0).getSubCategories().size());
                },
                ()->{
                    Assertions.assertEquals(1,categoryResponseList.get(0).getSubCategories().get(0).getSubCategories().get(0).getCategorySec());
                    Assertions.assertEquals(2,categoryResponseList.get(0).getSubCategories().get(0).getSubCategories().get(1).getCategorySec());
                    Assertions.assertEquals(3,categoryResponseList.get(0).getSubCategories().get(0).getSubCategories().get(2).getCategorySec());
                }
        );

        Mockito.verify(categoryRepository,Mockito.times(1)).findAllByBlogId(Mockito.anyLong());

    }

    @Test
    @DisplayName("계층적 카테고리 리스트 : 블로그가 존재하지 않을 때")
    void getAllCategories_exception_case1() {

        Mockito.when(blogRepository.existByBlogId(Mockito.anyLong())).thenReturn(false);

        Assertions.assertThrows(BadRequestException.class,()->{
            categoryService.getAllCategories(1l);
        });

        Mockito.verify(blogRepository,Mockito.times(1)).existByBlogId(Mockito.anyLong());
        Mockito.verify(categoryRepository,Mockito.never()).findAllByBlogId(Mockito.anyLong());
    }

}