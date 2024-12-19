package com.nhnacademy.blog.tag.repository;

import com.nhnacademy.blog.tag.domain.Tag;
import com.nhnacademy.blog.tag.dto.TagCreateRequest;
import com.nhnacademy.blog.tag.dto.TagResponse;
import com.nhnacademy.blog.tag.dto.TagUpdateRequest;

import java.util.Optional;

public interface TagRepository {
    void save(Tag tag);
    void update(TagUpdateRequest tagUpdateRequest);
    Optional<Tag> findById(Long tagId);
    Optional<Tag> findByTagName(String tagName);
    void deleteByTagId(Long tagId);
    void deleteByTagName(String tagName);
    boolean existsByTagId(Long tagId);
    boolean existsByTagName(String tagName);
}
