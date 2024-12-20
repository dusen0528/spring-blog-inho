package com.nhnacademy.blog.post.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.blog.category.domain.Category;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    private final Long postId;
    private final Long blogId;

    private final Long createdMbNo;
    private final String createdMbName;

    private final Long updatedMbNo;
    private final String updatedMbName;

    private final String postTitle;
    private final String postContent;
    private final boolean postIsPublic;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final List<CategoryInfo> categoryInfoList;
    private final List<TagInfo> tagInfoList;

    public PostResponse(Long postId, Long blogId, Long createdMbNo, String createdMbName, Long updatedMbNo, String updatedMbName, String postTitle, String postContent, boolean postIsPublic, LocalDateTime createdAt, LocalDateTime updatedAt, List<CategoryInfo> categoryInfoList, List<TagInfo> tagInfoList) {
        this.postId = postId;
        this.blogId = blogId;
        this.createdMbNo = createdMbNo;
        this.createdMbName = createdMbName;
        this.updatedMbNo = updatedMbNo;
        this.updatedMbName = updatedMbName;
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postIsPublic = postIsPublic;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.categoryInfoList = categoryInfoList;
        this.tagInfoList = tagInfoList;
    }

    public Long getPostId() {
        return postId;
    }

    public Long getBlogId() {
        return blogId;
    }

    public Long getCreatedMbNo() {
        return createdMbNo;
    }

    public String getCreatedMbName() {
        return createdMbName;
    }

    public Long getUpdatedMbNo() {
        return updatedMbNo;
    }

    public String getUpdatedMbName() {
        return updatedMbName;
    }

    public String getPostTitle() {
        return postTitle;
    }

    public String getPostContent() {
        return postContent;
    }

    public boolean isPostIsPublic() {
        return postIsPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<CategoryInfo> getCategoryInfoList() {
        return categoryInfoList;
    }

    public List<TagInfo> getTagInfoList() {
        return tagInfoList;
    }

    @Override
    public String toString() {
        return "PostResponse{" +
                "postId=" + postId +
                ", blogId=" + blogId +
                ", createdMbNo=" + createdMbNo +
                ", createdMbName='" + createdMbName + '\'' +
                ", updatedMbNo=" + updatedMbNo +
                ", updatedMbName='" + updatedMbName + '\'' +
                ", postTitle='" + postTitle + '\'' +
                ", postContent='" + postContent + '\'' +
                ", postIsPublic=" + postIsPublic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", categoryInfoList=" + categoryInfoList +
                ", tagInfoList=" + tagInfoList +
                '}';
    }

    public static class CategoryInfo{

        private final long categoryId;
        private final String categoryName;
        private final Integer topicId;
        private final String topicName;

        /*
            @JsonCreator 어노테이션은 Jackson 라이브러리에서 JSON 데이터를 Java 객체로 역직렬화할 때 생성자를 지정하기 위해 사용됩니다.
            이를 통해 JSON 데이터를 특정 생성자를 통해 객체로 만들 수 있습니다.
         */
        @JsonCreator
        public CategoryInfo(@JsonProperty("categoryId") long categoryId, @JsonProperty("categoryName") String categoryName, @JsonProperty("topicId") Integer topicId, @JsonProperty("topicName") String topicName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.topicId = topicId;
            this.topicName = topicName;
        }

        public long getCategoryId() {
            return categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public Integer getTopicId() {
            return topicId;
        }

        public String getTopicName() {
            return topicName;
        }

        @Override
        public String toString() {
            return "CategoryInfo{" +
                    "categoryId=" + categoryId +
                    ", categoryName='" + categoryName + '\'' +
                    ", topicId=" + topicId +
                    ", topicName='" + topicName + '\'' +
                    '}';
        }
    }

    public static class TagInfo{
        private final long tagId;
        private final String tagName;

        public TagInfo(@JsonProperty("tagId") long tagId, @JsonProperty("tagName") String tagName) {
            this.tagId = tagId;
            this.tagName = tagName;
        }

        public long getTagId() {
            return tagId;
        }

        public String getTagName() {
            return tagName;
        }

        @Override
        public String toString() {
            return "TagInfo{" +
                    "tagId=" + tagId +
                    ", tagName='" + tagName + '\'' +
                    '}';
        }
    }
}
