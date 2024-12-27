package com.nhnacademy.blog.topic.repository.impl;

import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.topic.domain.Topic;
import com.nhnacademy.blog.topic.dto.TopicUpdateRequestDto;
import com.nhnacademy.blog.topic.repository.TopicRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@SuppressWarnings("java:S1192")


@Repository
public class JdbcTopicRepository implements TopicRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTopicRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void save(Topic topic) {

        String sql = """
                    insert into topics set
                        topic_pid = ?,
                        topic_name = ?,
                        topic_sec = ?,
                        created_at = ?
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            if(Objects.nonNull(topic.getTopicPid())){
                psmt.setInt(index++,topic.getTopicPid());
            }else{
                psmt.setNull(index++,Types.INTEGER);
            }
            psmt.setString(index++, topic.getTopicName());
            psmt.setInt(index++, topic.getTopicSec());
            psmt.setTimestamp(index++, Timestamp.valueOf(topic.getCreatedAt()));
            return psmt;
        },keyHolder);

        if(rows>0){
            ReflectionUtils.setField(topic,"topicId", Objects.requireNonNull(keyHolder.getKey()).intValue());
        }

    }

    @Override
    public void update(TopicUpdateRequestDto topicUpdateRequestDto) {

        String sql = """
                    update topics set
                        topic_pid = ?,
                        topic_name = ?,
                        topic_sec = ?,
                        updated_at = now()
                    where topic_id = ?
                """;

        jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index = 1;
            if(Objects.nonNull(topicUpdateRequestDto.getTopicPid())){
                psmt.setInt(index++, topicUpdateRequestDto.getTopicPid());
            }else{
                psmt.setNull(index++, Types.INTEGER);
            }

            psmt.setString(index++, topicUpdateRequestDto.getTopicName());
            psmt.setInt(index++, topicUpdateRequestDto.getTopicSec());
            psmt.setInt(index++, topicUpdateRequestDto.getTopicId());
            return psmt;
        });

    }

    @Override
    public void deleteByTopicId(Integer topicId) {

        String sql = """
                    delete from topics where topic_id = ?
                """;
        jdbcTemplate.update(sql,topicId);
    }

    @Override
    public Optional<Topic> findByTopicId(Integer topicId) {

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

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql,new TopicRowMapper(),topicId)
        );
    }

    @Override
    public List<Topic> findAll(Integer topicPid) {

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

        if(Objects.isNull(topicPid)){
            return jdbcTemplate.query(sb.toString(),new TopicRowMapper());
        }
        return jdbcTemplate.query(sb.toString(),new TopicRowMapper(),topicPid);
    }

    @Override
    public boolean existByTopicId(Integer topicId) {
        String sql = """
                    select 1 from topics where topic_id = ?                
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, topicId));
    }

    private static class TopicRowMapper implements RowMapper<Topic> {

        @Override
        public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {

            Integer dbTopicId = rs.getInt("topic_id");
            Integer dbTopicPid = Objects.nonNull(rs.getObject("topic_pid")) ? rs.getInt("topic_pid") : null;
            String dbTopicName = rs.getString("topic_name");
            Integer dbTopicSec = rs.getInt("topic_sec");
            LocalDateTime dbCreatedAt = Objects.nonNull(rs.getObject("created_at")) ? rs.getTimestamp("created_at").toLocalDateTime() : null;
            LocalDateTime dbUpdatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null ;

            return Topic.ofExistingTopic(
                    dbTopicId,
                    dbTopicPid,
                    dbTopicName,
                    dbTopicSec,
                    dbCreatedAt,
                    dbUpdatedAt
            );
        }
    }
}