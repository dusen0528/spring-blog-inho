package com.nhnacademy.blog.topic.domain;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import jakarta.persistence.EntityManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
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

    @Test
    @DisplayName("자식 추가")
    @Rollback(false)
    void  addChildTest(){

        Topic parentTopic = Topic.ofNewRootTopic("language",1);
        parentTopic.addChildTopic(Topic.ofNewSubTopic(parentTopic,"java",1));
        parentTopic.addChildTopic(Topic.ofNewSubTopic(parentTopic,"c#",2));
        parentTopic.addChildTopic(Topic.ofNewSubTopic(parentTopic,"go",3));
        entityManager.persist(parentTopic);
        entityManager.flush();
        entityManager.clear();

        Topic topic = entityManager.find(Topic.class, parentTopic.getTopicId());

        log.debug("childrenTopics: {}", topic.getChildrenTopics().size());
        log.debug("topic: {}", topic);

        Assertions.assertEquals(3, topic.getChildrenTopics().size());
    }

    @Test
    @DisplayName("자식-topic-삭체")
    void removeChildTest(){
        Topic parentTopic = Topic.ofNewRootTopic("language",1);
        parentTopic.addChildTopic(Topic.ofNewSubTopic(parentTopic,"java",1));
        parentTopic.addChildTopic(Topic.ofNewSubTopic(parentTopic,"c#",2));
        parentTopic.addChildTopic(Topic.ofNewSubTopic(parentTopic,"go",3));
        entityManager.persist(parentTopic);

        List<Topic> childrenTopics = parentTopic.getChildrenTopics();
        log.debug("childrenTopics: {}", childrenTopics);

        Topic removeTarget = entityManager.find(Topic.class, childrenTopics.get(0).getTopicId());
        parentTopic.removeChildTopic(removeTarget);

        entityManager.flush();
        entityManager.clear();

        Topic topic = entityManager.find(Topic.class, parentTopic.getTopicId());
        log.debug("topic: {}", topic);
        Assertions.assertEquals(2, topic.getChildrenTopics().size());

        Topic removedTopic = entityManager.find(Topic.class, removeTarget.getTopicId());
        Assertions.assertNull(removedTopic);
    }
}