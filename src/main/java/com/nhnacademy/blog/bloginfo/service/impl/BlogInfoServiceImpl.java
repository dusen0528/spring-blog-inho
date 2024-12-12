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
import com.nhnacademy.blog.common.annotation.Qualifier;
import com.nhnacademy.blog.common.annotation.stereotype.Service;
import com.nhnacademy.blog.common.exception.ConflictException;

@Service(name = BlogInfoServiceImpl.BEAN_NAME)
public class BlogInfoServiceImpl implements BlogInfoService {
    public static final String BEAN_NAME = "blogInfoService";

    private final BlogRepository blogRepository;

    public BlogInfoServiceImpl(@Qualifier(JdbcBlogRepository.BEAN_NAME) BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    public BlogResponse createBlog(BlogCreateRequest blogCreateRequest) {
        //1.처음 생성된 블로그라면(메인블로그기 존재하지 않다면) isblogMain = true로 설정한다.
        //회원번호로, 블로그조회
        boolean existMainBlog = blogRepository.existMainBlogByMbNo(blogCreateRequest.getMbNo());
        boolean isBlogMain = existMainBlog  ? false : true;

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

    }

    @Override
    public void updateBlogVisibility(BlogVisibilityUpdateRequest blogVisibilityUpdateRequest) {

    }

    @Override
    public BlogResponse getBlog(long blogId) {
        return null;
    }

}
