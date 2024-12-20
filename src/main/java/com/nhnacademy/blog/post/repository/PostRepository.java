package com.nhnacademy.blog.post.repository;

import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.dto.PostResponse;
import com.nhnacademy.blog.post.dto.PostSearchParam;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;

import java.util.Optional;

public interface PostRepository {

    void save(Post post);
    void update(PostUpdateRequest postUpdateRequest);
    void deleteByPostId(Long postId);
    void updateByPostIdAndPostIsPublic(Long postId, Boolean isPublic);
    boolean existsByPostId(Long postId);
    Optional<Post> findByPostId(Long postId);
    Page<PostResponse> findAllByPageableAndPostSearchRequest(Pageable pageable, PostSearchParam postSearchParam);
    Long totalRowsByPostSearchRequest(Pageable pageable, PostSearchParam postSearchParam);

}
