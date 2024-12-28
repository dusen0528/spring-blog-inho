package com.nhnacademy.blog.bloginfo.service.impl;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogCreateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogVisibilityUpdateRequest;
import com.nhnacademy.blog.bloginfo.repository.JpaBlogRepository;
import com.nhnacademy.blog.bloginfo.service.BlogInfoService;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.JpaBlogMemberMappingRepository;
import com.nhnacademy.blog.common.exception.ConflictException;
import com.nhnacademy.blog.common.exception.ForbiddenException;
import com.nhnacademy.blog.common.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
//@Service
public class BlogInfoServiceImpl implements BlogInfoService {
    private final JpaBlogRepository blogRepository;
    private final JpaBlogMemberMappingRepository blogMemberMappingRepository;

    public BlogInfoServiceImpl(
            JpaBlogRepository blogRepository,
            JpaBlogMemberMappingRepository blogMemberMappingRepository
    ) {
        this.blogRepository = blogRepository;
        this.blogMemberMappingRepository = blogMemberMappingRepository;
    }

    @Transactional
    @Override
    public BlogResponse createBlog(BlogCreateRequest blogCreateRequest) {

        //1.처음 생성된 블로그라면(메인블로그기 존재하지 않다면) isblogMain = true로 설정한다.
        boolean existMainBlog = blogRepository.existMainBlogByMbNo(blogCreateRequest.getMbNo());

        //블로그가 존재하지 않다면 isBlogMain=true 설정
        boolean isBlogMain = !existMainBlog;

        //2.blog_fid 중복여부 체크
        boolean existBlogFid = blogRepository.existsByBlogFid(blogCreateRequest.getBlogFid());

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

    @Transactional
    @Override
    public BlogResponse updateBlog(BlogUpdateRequest blogUpdateRequest) {

        //blog 존재여부 체크
        checkExistBlog(blogUpdateRequest.getBlogId());

        //blog 소유자 체크
        checkOwner(blogUpdateRequest.getBlogId(),MemberThreadLocal.getMemberNo());

        if(Boolean.TRUE.equals(blogUpdateRequest.isBlogMain())){
            long mbNo = MemberThreadLocal.getMemberNo();

            //모든 blog 리스트 조회
            List<Blog> blogList = blogRepository.findAllBlogs(mbNo,"ROLE_OWNER");

            for(Blog blog : blogList){
                //대상 블로그를 제외한 나머지 블로그들의 blog_ㅡmain 값을 false로 변경
                if(blog.getBlogId().equals(blogUpdateRequest.getBlogId())){
                    blog.update(
                            blogUpdateRequest.getBlogName(),
                            blogUpdateRequest.getBlogMbNickname(),
                            blogUpdateRequest.getBlogDescription(),
                            true
                    );
                }else{
                    blog.enableBlogPublicAccess(false);
                }
            }
        }

        return getBlog(blogUpdateRequest.getBlogId());
    }

    @Transactional
    @Override
    public void updateBlogVisibility(BlogVisibilityUpdateRequest blogVisibilityUpdateRequest) {
        //blog 존재여부 체크
        checkExistBlog(blogVisibilityUpdateRequest.getBlogId());

        //blog 소유자 체크
        checkOwner(blogVisibilityUpdateRequest.getBlogId(), MemberThreadLocal.getMemberNo());

        Optional<Blog> blogOptional = blogRepository.findById(blogVisibilityUpdateRequest.getBlogId());
        blogOptional.ifPresent(blog -> {
            blog.enableBlogPublicAccess(blogVisibilityUpdateRequest.getBlogIsPublic());
        });
    }

    @Override
    public BlogResponse getBlog(long blogId) {
        Optional<Blog> blogOptional = blogRepository.findById(blogId);

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
        boolean flag = blogRepository.existsById(blogId);
        if(!flag) {
            throw new NotFoundException("Blog not found:%S".formatted(blogId));
        }
    }

    private void checkOwner(long blogId, long mbNo) {
        Optional<BlogMemberMapping> blogMemberMappingOptional =  blogMemberMappingRepository.findByMbNoAndBlogId(mbNo, blogId);

        //블로그의 맴버가 아니라면. ForbiddenException() 발생
        if(blogMemberMappingOptional.isEmpty() || !blogMemberMappingOptional.get().getRoleId().equals("ROLE_OWNER") ) {
            throw new ForbiddenException();
        }
    }

}
