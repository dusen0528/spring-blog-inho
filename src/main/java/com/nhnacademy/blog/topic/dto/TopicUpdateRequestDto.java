package com.nhnacademy.blog.topic.dto;

public class TopicUpdateRequestDto {
    private final Integer topicId;
    private final Integer topicPid;
    private final String topicName;
    private final Integer topicSec;

    public TopicUpdateRequestDto(Integer topicId, Integer topicPid, String topicName, Integer topicSec) {
        this.topicId = topicId;
        this.topicPid = topicPid;
        this.topicName = topicName;
        this.topicSec = topicSec;
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
}