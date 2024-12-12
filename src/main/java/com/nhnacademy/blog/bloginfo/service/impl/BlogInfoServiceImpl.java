package com.nhnacademy.blog.bloginfo.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogCreateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogVisibilityUpdateRequest;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.bloginfo.repository.impl.JdbcBlogRepository;
import com.nhnacademy.blog.bloginfo.service.BlogInfoService;
import com.nhnacademy.blog.blogmember.domain.BlogMembersMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMembersMappingRepository;
import com.nhnacademy.blog.blogmember.repository.impl.JdbcBlogMembersMappingRepository;
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.common.exception.NotFoundException;

import java.util.Optional;

@Service(name = BlogInfoServiceImpl.BEAN_NAME)
public class BlogInfoServiceImpl implements BlogInfoService {
    public static final String BEAN_NAME = "blogInfoService";

    private final BlogRepository blogRepository;
    private final BlogMembersMappingRepository blogMembersMappingRepository;

    public BlogInfoServiceImpl(
            @Qualifier(JdbcBlogRepository.BEAN_NAME) BlogRepository blogRepository,
            @Qualifier(JdbcBlogMembersMappingRepository.BEAN_NAME) BlogMembersMappingRepository blogMembersMappingRepository
    ) {
        this.blogRepository = blogRepository;
        this.blogMembersMappingRepository = blogMembersMappingRepository;
    }

    @Override
    public BlogResponse createBlog(BlogCreateRequest blogCreateRequest) {
        //1.처음 생성된 블로그라면(메인블로그기 존재하지 않다면) isblogMain = true로 설정한다.
        boolean existMainBlog = blogRepository.existMainBlogByMbNo(blogCreateRequest.getMbNo());
        //블로그가 존재하지 않다면 isBlogMain=true 설정
        boolean isBlogMain = !existMainBlog;

        //2.blog_fid 중복여부 체크
        boolean existBlogFid = blogRepository.existByBlogFid(blogCreateRequest.getBlogFid());

        if(existBlogFid) {
           throw new ConflictException("exist blog  fid : %s".formatted(blogCreateRequest.getBlogFid()));
        }

        //3.Blog 객체 생성
        Blog blog = Blog.ofNewBlog(
                blogCreateRequest.getBlogFid(),
                isBlogMain,
                blogCreateRequest.getBlogName(),
                blogCreateRequest.getBlogMbNickname(),
                blogCreateRequest.getBlogDescription()
        );

        blogRepository.save(blog);

        return getBlog(blog.getBlogId());
    }

    @Override
    public void updateBlog(BlogUpdateRequest blogUpdateRequest) {
        //blog 존재여부 체크
        checkExistBlog(blogUpdateRequest.getBlogId());

        //blog 소유자 체크
        checkOwner(blogUpdateRequest.getBlogId(),MemberThreadLocal.getMemberNo());

        blogRepository.update(blogUpdateRequest);
    }

    @Override
    public void updateBlogVisibility(BlogVisibilityUpdateRequest blogVisibilityUpdateRequest) {
        //blog 존재여부 체크
        checkExistBlog(blogVisibilityUpdateRequest.getBlogId());

        //blog 소유자 체크
        checkOwner(blogVisibilityUpdateRequest.getBlogId(), MemberThreadLocal.getMemberNo());

        blogRepository.updateByBlogIsPublic(blogVisibilityUpdateRequest.getBlogId(),blogVisibilityUpdateRequest.getBlogIsPublic());
    }

    @Override
    public BlogResponse getBlog(long blogId) {
        Optional<Blog> blogOptional = blogRepository.findByBlogId(blogId);

        //blog 존재여부 체크
        if(blogOptional.isEmpty()){
            throw new NotFoundException();
        }

        Blog blog = blogOptional.get();

        return new BlogResponse(
                blog.getBlogId(),
                blog.getBlogFid(),
                blog.isBlogMain(),
                blog.getBlogName(),
                blog.getBlogMbNickname(),
                blog.getBlogDescription(),
                blog.getBlogIsPublic(),
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }

    private void checkExistBlog(long blogId) {
        boolean flag = blogRepository.existByBlogId(blogId);
        if(flag) {
            throw new ConflictException("exist blog id : %s".formatted(blogId));
        }
    }

    private void checkOwner(long blogId, long mbNo) {

        Optional<BlogMembersMapping> blogMembersMappingOptional =  blogMembersMappingRepository.findByBlogMembersId(mbNo);

        if(blogMembersMappingOptional.isEmpty()) {
            throw new ForbiddenException();
        }

        if(!blogMembersMappingOptional.get().getBlogId().equals(blogId)){
            throw new ForbiddenException();
        }

        if(!blogMembersMappingOptional.get().getRoleId().equals("ROLE_OWNER")){
            throw new ForbiddenException();
        }
    }

}
