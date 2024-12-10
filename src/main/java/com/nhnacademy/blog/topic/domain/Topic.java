package com.nhnacademy.blog.topic.domain;

import java.time.LocalDateTime;

public class Topic {

    private final Integer topicId;
    private final Integer topicPid;
    private final String topicName;
    private final Integer topicSec;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private Topic(Integer topicId, Integer topicPid, String topicName, Integer topicSec, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.topicId = topicId;
        this.topicPid = topicPid;
        this.topicName = topicName;
        this.topicSec = topicSec;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Topic ofNewRootTopic(String topicName,Integer topicSec) {
        return new Topic(null,null,topicName, topicSec , LocalDateTime.now(), null);
    }

    public static Topic ofNewSubTopic(Integer topicPid, String topicName, Integer topicSec) {
        return new Topic(null,topicPid,topicName, topicSec , LocalDateTime.now(), null);
    }

    public static Topic ofExistingTopic(Integer topicId, Integer topicPid, String topicName,Integer topicSec, LocalDateTime createdAt, LocalDateTime updatedAt){
        return new Topic(topicId,topicPid,topicName, topicSec , createdAt, updatedAt);
    }

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
