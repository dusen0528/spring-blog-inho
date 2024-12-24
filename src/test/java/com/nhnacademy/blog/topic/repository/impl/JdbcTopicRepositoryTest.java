package com.nhnacademy.blog.topic.repository.impl;

import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.topic.domain.Topic;
import com.nhnacademy.blog.topic.dto.TopicUpdateRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Optional;

@Slf4j
class JdbcTopicRepositoryTest {

    static JdbcTopicRepository topicRepository;

    @BeforeAll
    static void beforeAll() {
        ApplicationContext context = ContextHolder.getApplicationContext();
        topicRepository = (JdbcTopicRepository) context.getBean("jdbcTopicRepository");
    }

    @BeforeEach
    void setUp(){
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown(){
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("topic-저장")
    void save() {
        //given
        Topic topic = Topic.ofNewRootTopic("IT",1);

        //when
        topicRepository.save(topic);

        //then
        Optional<Topic> topicOptional = topicRepository.findByTopicId(topic.getTopicId());
        Assertions.assertAll(
                ()->Assertions.assertEquals(topic.getTopicId(),topicOptional.get().getTopicId()),
                ()->Assertions.assertNull(topicOptional.get().getTopicPid()),
                ()->Assertions.assertEquals(topic.getTopicName(),topicOptional.get().getTopicName()),
                ()->Assertions.assertNotNull(topicOptional.get().getCreatedAt()),
                ()->Assertions.assertNull(topicOptional.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("sub-topic-저장")
    void save_subTopic() {
        //given
        Topic topic = Topic.ofNewRootTopic("IT",1);
        topicRepository.save(topic);
        Topic subTopic = Topic.ofNewSubTopic(topic.getTopicId(),"IT/인터넷",1);
        topicRepository.save(subTopic);

        //when
        Optional<Topic> subTopicOptional = topicRepository.findByTopicId(subTopic.getTopicId());

        //then
        Assertions.assertAll(
                ()->Assertions.assertEquals(subTopic.getTopicId(),subTopicOptional.get().getTopicId()),
                ()->Assertions.assertNotNull(subTopicOptional.get().getTopicPid()),
                ()->Assertions.assertEquals(subTopic.getTopicName(),subTopicOptional.get().getTopicName()),
                ()->Assertions.assertNotNull(subTopicOptional.get().getCreatedAt()),
                ()->Assertions.assertNull(subTopicOptional.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("topic-수정")
    void update() {

        //given
        Topic topic = Topic.ofNewRootTopic("IT",1);
        topicRepository.save(topic);

        //when
        TopicUpdateRequestDto topicUpdateRequestDto = new TopicUpdateRequestDto(topic.getTopicId(),null,"IT/프로그래밍",10);
        topicRepository.update(topicUpdateRequestDto);

        //then
        Optional<Topic> actual = topicRepository.findByTopicId(topic.getTopicId());
        Assertions.assertAll(
                ()->Assertions.assertEquals(topicUpdateRequestDto.getTopicName(),actual.get().getTopicName()),
                ()->Assertions.assertEquals(topicUpdateRequestDto.getTopicSec(),actual.get().getTopicSec()),
                ()->Assertions.assertNotNull(actual.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("topic-삭제")
    void deleteByTopicId() {
        Topic topic = Topic.ofNewRootTopic("IT",1);
        topicRepository.save(topic);
        topicRepository.deleteByTopicId(topic.getTopicId());

        boolean actual = topicRepository.existByTopicId(topic.getTopicId());
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("topic-조회-byTopicId")
    void findByTopicId() {
        Topic topic = Topic.ofNewRootTopic("IT",1);
        topicRepository.save(topic);
        Optional<Topic> actual = topicRepository.findByTopicId(topic.getTopicId());

        Assertions.assertAll(
                ()->Assertions.assertEquals(topic.getTopicId(),actual.get().getTopicId()),
                ()->Assertions.assertNull(actual.get().getTopicPid()),
                ()->Assertions.assertEquals(topic.getTopicName(),actual.get().getTopicName()),
                ()->Assertions.assertNotNull(actual.get().getCreatedAt()),
                ()->Assertions.assertNull(actual.get().getUpdatedAt())
        );
    }

    @Test
    @DisplayName("topic -조회 (Root)")
    @Disabled
    void findAll() {

        Topic topic1 = Topic.ofNewRootTopic("주제없음",1);
        Topic topic2 = Topic.ofNewRootTopic("라이프",2);
        Topic topic3 = Topic.ofNewRootTopic("여행*맛집",3);
        Topic topic4 = Topic.ofNewRootTopic("문화*연예",4);
        Topic topic5 = Topic.ofNewRootTopic("IT",5);
        Topic topic6 = Topic.ofNewRootTopic("스포츠",6);
        Topic topic7 = Topic.ofNewRootTopic("시사",7);
        Topic topic8 = Topic.ofNewRootTopic("이벤트",8);

        topicRepository.save(topic1);
        topicRepository.save(topic2);
        topicRepository.save(topic3);
        topicRepository.save(topic4);
        topicRepository.save(topic5);
        topicRepository.save(topic6);
        topicRepository.save(topic7);
        topicRepository.save(topic8);

        List<Topic> topics = topicRepository.findAll(null);
        log.debug("count of topics : {}", topics.size());

        Assertions.assertFalse(topics.isEmpty());


        Assertions.assertAll(
            ()->Assertions.assertEquals(topic1.getTopicId(),topics.get(0).getTopicId()),
            ()->Assertions.assertEquals(topic2.getTopicId(),topics.get(1).getTopicId()),
            ()->Assertions.assertEquals(topic3.getTopicId(),topics.get(2).getTopicId()),
            ()->Assertions.assertEquals(topic4.getTopicId(),topics.get(3).getTopicId()),
            ()->Assertions.assertEquals(topic5.getTopicId(),topics.get(4).getTopicId()),
            ()->Assertions.assertEquals(topic6.getTopicId(),topics.get(5).getTopicId()),
            ()->Assertions.assertEquals(topic7.getTopicId(),topics.get(6).getTopicId()),
            ()->Assertions.assertEquals(topic8.getTopicId(),topics.get(7).getTopicId())
        );

    }

    @Test
    @DisplayName("topic -조회 (sub)")
    void findAll_subTopic() {

        Topic topic1 = Topic.ofNewRootTopic("주제없음",1);
        Topic topic2 = Topic.ofNewRootTopic("라이프",2);
        Topic topic3 = Topic.ofNewRootTopic("여행*맛집",3);
        Topic topic4 = Topic.ofNewRootTopic("문화*연예",4);
        Topic topic5 = Topic.ofNewRootTopic("IT",5);
        Topic topic6 = Topic.ofNewRootTopic("스포츠",6);
        Topic topic7 = Topic.ofNewRootTopic("시사",7);
        Topic topic8 = Topic.ofNewRootTopic("이벤트",8);

        topicRepository.save(topic1);
        topicRepository.save(topic2);
        topicRepository.save(topic3);
        topicRepository.save(topic4);
        topicRepository.save(topic5);
        topicRepository.save(topic6);
        topicRepository.save(topic7);
        topicRepository.save(topic8);

        Topic subTopic1 = Topic.ofNewSubTopic(topic5.getTopicId(),"IT 인터넷",1);
        Topic subTopic2 = Topic.ofNewSubTopic(topic5.getTopicId(),"모바일",2);
        Topic subTopic3 = Topic.ofNewSubTopic(topic5.getTopicId(),"게임",3);
        Topic subTopic4 = Topic.ofNewSubTopic(topic5.getTopicId(),"과학",4);
        Topic subTopic5 = Topic.ofNewSubTopic(topic5.getTopicId(),"IT 제품리뷰",5);

        topicRepository.save(subTopic1);
        topicRepository.save(subTopic2);
        topicRepository.save(subTopic3);
        topicRepository.save(subTopic4);
        topicRepository.save(subTopic5);

        List<Topic>subTopics = topicRepository.findAll(topic5.getTopicId());
        log.debug("count of subTopics : {}", subTopics.size());

        Assertions.assertAll(
                ()->Assertions.assertEquals(subTopic1.getTopicId(),subTopics.get(0).getTopicId()),
                ()->Assertions.assertEquals(subTopic2.getTopicId(),subTopics.get(1).getTopicId()),
                ()->Assertions.assertEquals(subTopic3.getTopicId(),subTopics.get(2).getTopicId()),
                ()->Assertions.assertEquals(subTopic4.getTopicId(),subTopics.get(3).getTopicId()),
                ()->Assertions.assertEquals(subTopic5.getTopicId(),subTopics.get(4).getTopicId())
        );

    }

    @Test
    @DisplayName("topic-존재여부체크 : true")
    void existByTopicId() {
        Topic topic = Topic.ofNewRootTopic("IT",1);
        topicRepository.save(topic);
        boolean actual = topicRepository.existByTopicId(topic.getTopicId());
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("topic-존재여부체크 : false")
    void notExistByTopicId() {
        boolean actual = topicRepository.existByTopicId(-1);
        Assertions.assertFalse(actual);
    }
}