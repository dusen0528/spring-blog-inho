package com.nhnacademy.blog.topic.repository.impl;

import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.topic.domain.Topic;
import com.nhnacademy.blog.topic.dto.TopicUpdateRequestDto;
import com.nhnacademy.blog.topic.repository.TopicRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@SuppressWarnings("java:S1192")
@Repository(name = JdbcTopicRepository.BEAN_NAME)
public class JdbcTopicRepository implements TopicRepository {

    public static final String BEAN_NAME = "jdbcTopicRepository";

    @Override
    public void save(Topic topic) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    insert into topics set
                        topic_pid = ?,
                        topic_name = ?,
                        topic_sec = ?,
                        created_at = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            int index = 1;
            if(Objects.nonNull(topic.getTopicPid())){
                psmt.setInt(index++,topic.getTopicPid());
            }else{
                psmt.setNull(index++,Types.INTEGER);
            }
            psmt.setString(index++, topic.getTopicName());
            psmt.setInt(index++, topic.getTopicSec());
            psmt.setTimestamp(index++, Timestamp.valueOf(topic.getCreatedAt()));
            psmt.executeUpdate();
            try(ResultSet rs = psmt.getGeneratedKeys()) {
                if(rs.next()) {
                    ReflectionUtils.setField(topic,"topicId", rs.getInt(1));
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(TopicUpdateRequestDto topicUpdateRequestDto) {

        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    update topics set
                        topic_pid = ?,
                        topic_name = ?,
                        topic_sec = ?,
                        updated_at = now()
                    where topic_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index = 1;
            if(Objects.nonNull(topicUpdateRequestDto.getTopicPid())){
                psmt.setInt(index++, topicUpdateRequestDto.getTopicPid());
            }else{
                psmt.setNull(index++, Types.INTEGER);
            }

            psmt.setString(index++, topicUpdateRequestDto.getTopicName());
            psmt.setInt(index++, topicUpdateRequestDto.getTopicSec());
            psmt.setInt(index++, topicUpdateRequestDto.getTopicId());
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByTopicId(Integer topicId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from topics where topic_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setInt(1, topicId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<Topic> findByTopicId(Integer topicId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                        topic_id,
                        topic_pid,
                        topic_name,
                        topic_sec,
                        created_at,
                        updated_at
                    from topics where topic_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setInt(1, topicId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    Integer dbTopicId = rs.getInt("topic_id");
                    Integer dbTopicPid = Objects.nonNull(rs.getObject("topic_pid")) ? rs.getInt("topic_pid") : null;
                    String dbTopicName = rs.getString("topic_name");
                    Integer dbTopicSec = rs.getInt("topic_sec");
                    LocalDateTime dbCreatedAt = Objects.nonNull(rs.getObject("created_at")) ? rs.getTimestamp("created_at").toLocalDateTime() : null;
                    LocalDateTime dbUpdatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null ;

                    Topic topic = Topic.ofExistingTopic(
                        dbTopicId,dbTopicPid,dbTopicName,dbTopicSec,dbCreatedAt,dbUpdatedAt
                    );

                    return Optional.of(topic);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Topic> findAll(Integer topicPid) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        /* SQL 작성시 참고 합니다.
          - topic_pid is null : null인지 체크 합니다.
          - topic_pid = NULL : SQL 표준에서는 = NULL을 사용할 수 없습니다. NULL은 값이 없는 상태를 나타내므로, 비교 연산자로 직접 사용할 수 없음
        */

        StringBuilder sb = new StringBuilder();
        String sql = """
                select 
                    topic_id,
                    topic_pid,
                    topic_name,
                    topic_sec,
                    created_at,
                    updated_at 
                from 
                    topics 
                where 1
                """;
        sb.append(sql);

        if(Objects.nonNull(topicPid)){
            sb.append(" and topic_pid = ? ");
        }else{
            sb.append(" and topic_pid is null ");
        }
        sb.append( " order by topic_sec asc ");

        List<Topic> topics = new ArrayList<>();

        try(PreparedStatement psmt = connection.prepareStatement(sb.toString())){
            if(Objects.nonNull(topicPid)) {
                psmt.setInt(1, topicPid);
            }

            try(ResultSet rs = psmt.executeQuery()){
                while (rs.next()) {
                    Integer dbTopicId = rs.getInt("topic_id");
                    Integer dbTopicPid = Objects.nonNull(rs.getObject("topic_pid")) ? rs.getInt("topic_pid") : null;
                    String dbTopicName = rs.getString("topic_name");
                    Integer dbTopicSec = rs.getInt("topic_sec");
                    LocalDateTime dbCreatedAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime dbUpdatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null;
                    topics.add(Topic.ofExistingTopic(dbTopicId,dbTopicPid,dbTopicName,dbTopicSec,dbCreatedAt,dbUpdatedAt));
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return topics;
    }

    @Override
    public boolean existByTopicId(Integer topicId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    select 1 from topics where topic_id = ?                
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setInt(1, topicId);
            try(ResultSet rs = psmt.executeQuery();){
                if(rs.next()) {
                    return true;
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return false;
    }
}