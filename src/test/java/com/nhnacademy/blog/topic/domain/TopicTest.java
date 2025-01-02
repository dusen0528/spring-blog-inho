package com.nhnacademy.blog.topic.domain;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO#1-TEST TopicTest를 구현 합니다.
 */

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class TopicTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("topic 저장")
    void saveTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",1);
        entityManager.persist(topic);

        Topic newTopic = entityManager.find(Topic.class, topic.getTopicId());

        Assertions.assertNotNull(newTopic);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(topic.getTopicId(), newTopic.getTopicId());
                },
                ()->{
                    Assertions.assertEquals(1,newTopic.getTopicSec());
                },
                ()->{
                    Assertions.assertEquals("Internet", newTopic.getTopicName());
                },
                ()->{
                    Assertions.assertNotNull(newTopic.getCreatedAt());
                },
                ()->{
                    Assertions.assertNull(newTopic.getUpdatedAt());
                }
        );
    }

    @Test
    @DisplayName("topic 수정")
    void updateTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",1);
        entityManager.persist(topic);

        topic.update(null,"IT-Internet",2);

        entityManager.flush();

        Topic newTopic = entityManager.find(Topic.class, topic.getTopicId());

        Assertions.assertNotNull(newTopic);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(topic.getTopicId(), newTopic.getTopicId());
                },
                ()->{
                    Assertions.assertEquals(2,newTopic.getTopicSec());
                },
                ()->{
                    Assertions.assertEquals("IT-Internet", newTopic.getTopicName());
                },
                ()->{
                    Assertions.assertNotNull(newTopic.getCreatedAt());
                },
                ()->{
                    Assertions.assertNotNull(newTopic.getUpdatedAt());
                }
        );
    }

    @Test
    @DisplayName("topic 삭제")
    void deleteTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",1);
        entityManager.persist(topic);
        entityManager.flush();

        entityManager.remove(topic);
        entityManager.flush();
        Topic newTopic = entityManager.find(Topic.class, topic.getTopicId());
        Assertions.assertNull(newTopic);
    }
}