package com.nhnacademy.blog.tagging.repository.impl;

import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.tagging.domain.PostTag;
import com.nhnacademy.blog.tagging.repository.PostTagRepository;

import java.sql.*;
import java.util.Optional;

@Repository(JdbcPostTagRepository.BEAN_NAME)
public class JdbcPostTagRepository implements PostTagRepository {
    public static final String BEAN_NAME = "jdbcPostTagRepository";
    @Override
    public void save(PostTag postTag) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                INSERT INTO post_tag_mappings  set 
                    post_id = ?,
                    tag_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            psmt.setLong(1, postTag.getPostId());
            psmt.setLong(2, postTag.getTagId());
            psmt.executeUpdate();

            try(ResultSet rs = psmt.getGeneratedKeys()) {
                if(rs.next()) {
                    long postTagId = rs.getLong(1);
                    ReflectionUtils.setField(postTag, "postTagId", postTagId);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByPostTagId(Long postTagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from post_tag_mappings where post_tag_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postTagId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByPostIdAndTagId(Long postId, Long tagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                delete from post_tag_mappings where post_id = ? and tag_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.setLong(2, tagId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<PostTag> findByPostTagId(Long postTagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select 
                    post_tag_id,
                    post_id,
                    tag_id 
                from 
                    post_tag_mappings 
                where 
                    post_tag_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postTagId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbPostTagId = rs.getLong("post_tag_id");
                    long dbPostId = rs.getLong("post_id");
                    long dbTagId = rs.getLong("tag_id");
                    PostTag postTag = PostTag.ofExistingPostTag(dbPostTagId, dbPostId, dbTagId);
                    return Optional.of(postTag);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PostTag> findByPostIdAndTagId(Long postId, Long tagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select 
                    post_tag_id,
                    post_id,
                    tag_id 
                from 
                    post_tag_mappings 
                where 
                        post_id = ?
                    and tag_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.setLong(2, tagId);

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbPostTagId = rs.getLong(1);
                    long dbPostId = rs.getLong(2);
                    long dbTagId = rs.getLong(3);
                    PostTag postTag = PostTag.ofExistingPostTag(dbPostTagId, dbPostId, dbTagId);
                    return Optional.of(postTag);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByPostTagId(Long postTagId) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select 
                    1
                from 
                    post_tag_mappings 
                where 
                    post_tag_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postTagId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    return true;
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return false;
    }

    @Override
    public boolean existsByPostIdAndTagId(Long postId, Long tagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select 
                    1
                from 
                    post_tag_mappings 
                where 
                        post_id = ?
                    and tag_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.setLong(2, tagId);
            try(ResultSet rs = psmt.executeQuery()){
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
