package com.nhnacademy.blog.topic.repository;

import com.nhnacademy.blog.topic.domain.Topic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO#6 - TEST
 * - TopicRepository 이용해서 구현 합니다.
 * - MemberRepository를 참고하세요
 */
class TopicRepositoryTest {

    @Test
    @DisplayName("topic 저장")
    void saveTest(){

    }

    @Test
    @DisplayName("topic 수정")
    void updateTest(){

    }

    @Test
    @DisplayName("topic 삭제")
    void deleteTest(){

    }

    @Test
    @DisplayName("자식 추가")
    @Rollback(false)
    void  addChildTest(){

    }

    @Test
    @DisplayName("자식-topic-삭체")
    void removeChildTest(){

    }
}