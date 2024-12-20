package com.nhnacademy.blog.tagging.repository;

import com.nhnacademy.blog.tagging.domain.PostTag;

import java.util.Optional;

public interface PostTagRepository {

    void save(PostTag postTag);
    void deleteByPostTagId(Long postTagId);
    void deleteByPostIdAndTagId(Long postId, Long tagId);

    Optional<PostTag> findByPostTagId(Long postTagId);
    Optional<PostTag> findByPostIdAndTagId(Long postId, Long tagId);

    boolean existsByPostTagId(Long postTagId);
    boolean existsByPostIdAndTagId(Long postId, Long tagId);
}
