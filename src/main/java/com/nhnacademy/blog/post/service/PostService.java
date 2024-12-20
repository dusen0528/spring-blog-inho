package com.nhnacademy.blog.post.service;

import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.post.dto.PostCreateRequest;
import com.nhnacademy.blog.post.dto.PostResponse;
import com.nhnacademy.blog.post.dto.PostSearchParam;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;

public interface PostService {

    //post 등록
    PostResponse createPost(PostCreateRequest postCreateRequest);

    //post 수정
    PostResponse updatePost(PostUpdateRequest postUpdateRequest);

    //post 삭제
    void deletePost(Long postId);

    //post 조회
    PostResponse getPost(Long postId);

    //post 리스트
    Page<PostResponse> getPostList(Pageable pageable, PostSearchParam postSearchParam);

}
