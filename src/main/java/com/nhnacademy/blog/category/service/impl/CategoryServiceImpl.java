package com.nhnacademy.blog.category.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.blogmember.repository.impl.JdbcBlogMemberMappingRepository;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.*;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.category.repository.impl.JdbcCategoryRepository;
import com.nhnacademy.blog.category.service.CategoryService;
import com.nhnacademy.blog.category.util.CategoryUtils;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.common.exception.BadRequestException;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import com.nhnacademy.blog.topic.repository.TopicRepository;
import com.nhnacademy.blog.topic.repository.impl.JdbcTopicRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
//api/blogs/{blog-id}/categories/{category-id}
@Service(name = CategoryServiceImpl.BEAN_NAME)
public class CategoryServiceImpl implements CategoryService {

    public static final String BEAN_NAME="categoryService";

    private final CategoryRepository categoryRepository;
    private final BlogRepository blogRepository;
    private final TopicRepository topicRepository;
    private final BlogMemberMappingRepository blogMemberMappingRepository;

    public CategoryServiceImpl(
            @Qualifier(value = JdbcCategoryRepository.BEAN_NAME) CategoryRepository categoryRepository,
            @Qualifier(value = JdbcBlogRepository.BEAN_NAME) BlogRepository blogRepository,
            @Qualifier(value = JdbcTopicRepository.BEAN_NAME) TopicRepository topicRepository,
            @Qualifier(value = JdbcBlogMemberMappingRepository.BEAN_NAME) BlogMemberMappingRepository blogMemberMappingRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.blogRepository = blogRepository;
        this.topicRepository = topicRepository;
        this.blogMemberMappingRepository = blogMemberMappingRepository;
    }

    @Override
    public CategoryResponse createRootCategory(RootCategoryCreateRequest rootCategoryCreateRequest) {

        //0.blog의 카테고리를 생성할 수 있는지 권한체크
        long memberNo = MemberThreadLocal.getMemberNo();
        checkOwner(rootCategoryCreateRequest.getBlogId(), memberNo);

        //1.블로그 존재여부 체크
        checkExistBlog(rootCategoryCreateRequest.getBlogId());

        //2.토픽 존재여부 체크
        checkExistTopic(rootCategoryCreateRequest.getTopicId());

        Category category = Category.ofNewRootCategory(
                rootCategoryCreateRequest.getBlogId(),
                rootCategoryCreateRequest.getTopicId(),
                rootCategoryCreateRequest.getCategoryName(),
                rootCategoryCreateRequest.getCategorySec()
        );

        categoryRepository.save(category);

        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category.getCategoryId());
        log.debug("categoryOptional:{}", categoryOptional.get());
        return new CategoryResponse(
                categoryOptional.get().getCategoryId(),
                categoryOptional.get().getCategoryPid(),
                categoryOptional.get().getBlogId(),
                categoryOptional.get().getTopicId(),
                categoryOptional.get().getCategoryName(),
                categoryOptional.get().getCategorySec()
        );
    }

    @Override
    public CategoryResponse createSubCategory(SubCategoryCreateRequest subCategoryCreateRequest) {

        //0.blog의 카테고리를 생성할 수 있는지 권한체크
        long memberNo = MemberThreadLocal.getMemberNo();
        checkOwner(subCategoryCreateRequest.getBlogId(), memberNo);

        //1.topic존재여부 체크
        checkExistTopic(subCategoryCreateRequest.getTopicId());

        //2.블로그 존재여부 체크
        checkExistBlog(subCategoryCreateRequest.getBlogId());

        //3.부모카테고리 존재여부 체크
        checkExistCategory(subCategoryCreateRequest.getCategoryPid());

        Category category  = Category.ofNewSubCategory(
                subCategoryCreateRequest.getCategoryPid(),
                subCategoryCreateRequest.getBlogId(),
                subCategoryCreateRequest.getTopicId(),
                subCategoryCreateRequest.getCategoryName(),
                subCategoryCreateRequest.getCategorySec());
        categoryRepository.save(category);

        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(category.getCategoryId());
        log.debug("categoryOptional:{}", categoryOptional.get());
        return new CategoryResponse(
                categoryOptional.get().getCategoryId(),
                categoryOptional.get().getCategoryPid(),
                categoryOptional.get().getBlogId(),
                categoryOptional.get().getTopicId(),
                categoryOptional.get().getCategoryName(),
                categoryOptional.get().getCategorySec()
        );

    }

    @Override
    public CategoryResponse updateRootCategory(RootCategoryUpdateRequest rootCategoryUpdateRequest) {

        //0.blog의 카테고리를 수정할 수 있는 권한체크
        long memberNo = MemberThreadLocal.getMemberNo();
        checkOwner(rootCategoryUpdateRequest.getBlogId(), memberNo);

        //1.블로그 존재여부 체크
        checkExistBlog(rootCategoryUpdateRequest.getBlogId());

        //2.category 체크
        checkExistCategory(rootCategoryUpdateRequest.getCategoryId());

        //3.topic 체크
        checkExistTopic(rootCategoryUpdateRequest.getTopicId());

        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest(
                rootCategoryUpdateRequest.getCategoryId(),
                null,
                rootCategoryUpdateRequest.getBlogId(),
                rootCategoryUpdateRequest.getTopicId(),
                rootCategoryUpdateRequest.getCategoryName(),
                rootCategoryUpdateRequest.getCategorySec()
        );

        //4.rootCategory 수정
        categoryRepository.update(categoryUpdateRequest);

        //4. 수정된 결과 반환
        Optional<Category> categoryOptional = categoryRepository.findByCategoryId(rootCategoryUpdateRequest.getCategoryId());

        return new CategoryResponse(
                categoryOptional.get().getCategoryId(),
                categoryOptional.get().getCategoryPid(),
                categoryOptional.get().getBlogId(),
                categoryOptional.get().getTopicId(),
                categoryOptional.get().getCategoryName(),
                categoryOptional.get().getCategorySec()
        );

    }

    @Override
    public CategoryResponse updateSubCategory(SubCategoryUpdateRequest subCategoryUpdateRequest) {
        //0.blog의 카테고리를 수정할 수 있는지 권한체크
        long memberNo = MemberThreadLocal.getMemberNo();
        checkOwner(subCategoryUpdateRequest.getBlogId(), memberNo);

        //1.블로그 존재여부 체크
        checkExistBlog(subCategoryUpdateRequest.getBlogId());

        //2.category 체크
        checkExistCategory(subCategoryUpdateRequest.getCategoryId());

        //3.부모카테고리 체크
        checkExistCategory(subCategoryUpdateRequest.getCategoryPid());

        //4.topic 체크
        checkExistTopic(subCategoryUpdateRequest.getTopicId());

        //5.rootCategory 수정
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest(
                subCategoryUpdateRequest.getCategoryId(),
                subCategoryUpdateRequest.getCategoryPid(),
                subCategoryUpdateRequest.getBlogId(),
                subCategoryUpdateRequest.getTopicId(),
                subCategoryUpdateRequest.getCategoryName(),
                subCategoryUpdateRequest.getCategorySec()
        );
        categoryRepository.update(categoryUpdateRequest);

        Optional<Category>  categoryOptional = categoryRepository.findByCategoryId(categoryUpdateRequest.getCategoryId());
        return new CategoryResponse(
                categoryOptional.get().getCategoryId(),
                categoryOptional.get().getCategoryPid(),
                categoryOptional.get().getBlogId(),
                categoryOptional.get().getTopicId(),
                categoryOptional.get().getCategoryName(),
                categoryOptional.get().getCategorySec()
        );
    }

    @Override
    public void deleteCategory(CategoryDeleteRequest categoryDeleteRequest) {
        //0.blog의 카테고리를 수정할 수 있는지 권한체크
        long memberNo = MemberThreadLocal.getMemberNo();
        checkOwner(categoryDeleteRequest.getCategoryId(), memberNo);

        //1. category 체크
        checkExistCategory(categoryDeleteRequest.getCategoryId());

        //2. subcategory가 존재하는지 체크, 서브카테고리가 존재 한다면 삭제불가
        checkExistSubCategory(categoryDeleteRequest.getCategoryId());

        //3. blog 존재여부 체크
        checkExistBlog(categoryDeleteRequest.getBlogId());

        //4. 추후 카테고리 생성시 post쪽 처리를 어떻게 할지 고민하기.

        //5. 카테고리 삭제
        categoryRepository.deleteByCategoryId(categoryDeleteRequest.getCategoryId());
    }

    @Override
    /**
     *카테고리를 계층적으로 정렬 후 반환 합니다.
     */
    public List<CategoryResponse> getAllCategories(long blogId) {
        //1.블로그 존재유무 체크
        checkExistBlog(blogId);

        List<CategoryResponse>  categoryList = categoryRepository.findAllByBlogId(blogId);
        return CategoryUtils.generateCategoryHierarchy(categoryList);
    }

    //blog 소유자체크
    private void checkOwner(long blogId, long mbNo) {
        Optional<BlogMemberMapping> blogMemberMappingOptional =  blogMemberMappingRepository.findByMbNoAndBlogId(mbNo, blogId);

        //블로그의 맴버가 아니라면. ForbiddenException() 발생
        if(blogMemberMappingOptional.isEmpty() || !blogMemberMappingOptional.get().getRoleId().equals("ROLE_OWNER") ) {
            throw new ForbiddenException();
        }
    }

    private void checkExistBlog(long blogId) {
        boolean existBlog = blogRepository.existByBlogId(blogId);
        if(!existBlog) {
            throw new BadRequestException("blog [%d] does not exist".formatted(blogId));
        }
    }

    private void checkExistTopic(int topicId) {
        if(Objects.nonNull(topicId)) {
            boolean existTopic = topicRepository.existByTopicId(topicId);
            if(!existTopic) {
                throw new BadRequestException("topic [%d] does not exist".formatted(topicId));
            }
        }
    }

    private void checkExistCategory(long categoryId){
        boolean existCategory = categoryRepository.existsByCategoryId(categoryId);
        if(!existCategory) {
            throw new NotFoundException("category [%d] does not exist".formatted(categoryId));
        }
    }

    private void checkExistSubCategory(long categoryId){
        boolean existSubCategory = categoryRepository.existsSubCategoryByCategoryId(categoryId);
        if(existSubCategory) {
           throw new ConflictException("%d 's subCategory already exist".formatted(categoryId));
        }
    }

}
