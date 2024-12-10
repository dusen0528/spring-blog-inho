package com.nhnacademy.blog.topic.repository;

import com.nhnacademy.blog.topic.domain.Topic;
import com.nhnacademy.blog.topic.dto.TopicUpdateRequestDto;

import java.util.List;
import java.util.Optional;

public interface TopicRepository {
    //주제 저장
    void save(Topic topic);
    //주제 수정
    void update(TopicUpdateRequestDto topicUpdateRequestDto);
    //수제 삭제
    void deleteByTopicId(Integer topicId);
    //주제 조회
    Optional<Topic> findByTopicId(Integer topicId);
    //topicPid를 기준으로 모든(주제) 조회
    List<Topic> findAll(Integer topicPid);
    //topic 존제하는지 체크
    boolean existByTopicId(Integer topicId);
}