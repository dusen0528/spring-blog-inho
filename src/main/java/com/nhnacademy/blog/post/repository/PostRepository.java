package com.nhnacademy.blog.post.repository;

import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.pageable.PageRequest;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.dto.PostSearchRequest;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;

import java.util.List;

public interface PostRepository {

    void save(Post post);
    void update(PostUpdateRequest postUpdateRequest);
    void deleteByPostId(Long postId);
    void updateByPostIdAndPostIsPublic(Long postId, Boolean isPublic);
    boolean existsByPostId(Long postId);
    Post findByPostId(Long postId);
    Page<Post> findAll(Pageable pageable);

}
