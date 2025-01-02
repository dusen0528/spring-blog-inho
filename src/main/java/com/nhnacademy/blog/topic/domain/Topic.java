package com.nhnacademy.blog.topic.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO#1  Member Entity를 참고하여 Topic Entity Mapping을 합니다.
 */

//TODO#1-1 @Entity annotation을 작성 합니다.
@Entity

//TODO#1-2 @Table annotation을 작성 합니다.
@Table(name = "topics",
    indexes = {
        @Index(name = "idx_topic_pid", columnList = "topic_pid", unique = false )
    }
)
public class Topic {
    //TODO#1-3 @Id, @GeneratedValue, @Column ... 등 에너테이션을 활용하여
    //Entity Mapping을 합니다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer topicId;
    @Column(nullable = false, length = 100)
    private String topicName;
    @Column(nullable = true)
    private Integer topicPid;
    @Column(nullable = false)
    private Integer topicSec=1;
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Topic(Integer topicId, Integer topicPid, String topicName, Integer topicSec, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.topicId = topicId;
        this.topicPid = topicPid;
        this.topicName = topicName;
        this.topicSec = topicSec;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //TODO#1-4 기본 생성자를 작성 합니다.
    public Topic() {

    }

    //TODO#1-5 엔티티가 영속화 되기전(@PrePersist)에 createdAt, updatedAt 설정 합니다.
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        this.updatedAt = null;
    }

    //TODO#1-6 업데이트 하기전(@PreUpdate)에 updateAt = 현재시간으로 설정 합니다.
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    //TODO#1-7 update메서드를 작성 합니다.
    public void update(Integer topicPid, String topicName, Integer topicSec) {
        this.topicPid = topicPid;
        this.topicName = topicName;
        this.topicSec = topicSec;
    }

    /**
     * TODO#1-8 Root topic 생성
     *  - topicId, topicPid느 null 입니다.
     */
    public static Topic ofNewRootTopic(String topicName,Integer topicSec) {
        return new Topic(null,null,topicName, topicSec , LocalDateTime.now(), null);
    }

    /**
     * TODO#1-9 SubTopic 생성
     * - topicId는 NULL 입니다.
     * @param topicPid
     * @param topicName
     * @param topicSec
     * @return
     */
    public static Topic ofNewSubTopic(Integer topicPid, String topicName, Integer topicSec) {
        return new Topic(null,topicPid,topicName, topicSec , LocalDateTime.now(), null);
    }

    /**
     * * TODO#1-10 getTopicId()를 참고하여 getter() 메서드를 작성 합니다.
     * @return
     */
    public Integer getTopicId() {
        return topicId;
    }

    public Integer getTopicPid() {
        return topicPid;
    }

    public String getTopicName() {
        return topicName;
    }

    public Integer getTopicSec() {
        return topicSec;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}