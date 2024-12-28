package com.nhnacademy.blog.topic.repository;

import com.nhnacademy.blog.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaTopicRepository extends JpaRepository<Topic, Integer> {

    List<Topic> findAllByOrderByTopicSecAsc();

    List<Topic> findAllByTopicPid(Integer topicPid);
}
