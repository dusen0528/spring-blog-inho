package com.nhnacademy.blog.bloginfo.service;

import com.nhnacademy.blog.bloginfo.dto.BlogCreateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.dto.BlogVisibilityUpdateRequest;

public interface BlogInfoService {

    //블로그 생성
    BlogResponse createBlog(BlogCreateRequest blogCreateRequest);

    //블로그 수정
    BlogResponse updateBlog(BlogUpdateRequest blogUpdateRequest);

    //블로그 상태변경
    void updateBlogVisibility(BlogVisibilityUpdateRequest blogVisibilityUpdateRequest);

    //블로그 정보조회
    BlogResponse getBlog(long blogId);

    //블로그 삭제는 당장 고려하지 않음.

}
