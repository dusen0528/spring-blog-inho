package com.nhnacademy.blog.topic.repository;

import com.nhnacademy.blog.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO#6-TopicRepository 구현
 */
public interface TopicRepository extends JpaRepository<Topic, Long> {

}
