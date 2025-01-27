package com.nhnacademy.blog.topic.repository;

import com.nhnacademy.blog.topic.domain.Topic;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findTopicsByParentTopicIsNullOrderByTopicSecAsc();
    List<Topic> findTopicsByParentTopic_topicIdOrderByTopicSecAsc(Integer topicId);
}
