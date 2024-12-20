package com.nhnacademy.blog.tag.repository.impl;

import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.tag.domain.Tag;
import com.nhnacademy.blog.tag.dto.TagUpdateRequest;
import com.nhnacademy.blog.tag.repository.TagRepository;

import java.sql.*;
import java.util.Optional;

@Repository(JdbcTagRepository.BEAN_NAME)
public class JdbcTagRepository implements TagRepository {
    public static final String BEAN_NAME = "jdbcTagRepository";

    @Override
    public void save(Tag tag) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                insert into tags set
                tag_name=?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            psmt.setString(1, tag.getTagName());
            psmt.executeUpdate();

            try(ResultSet rs = psmt.getGeneratedKeys()){
                if(rs.next()) {
                    ReflectionUtils.setField(tag,"tagId", rs.getLong(1));
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(TagUpdateRequest tagUpdateRequest) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                update tags set
                tag_name=?
                where tag_id=?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setString(1, tagUpdateRequest.getTagName());
            psmt.setLong(2, tagUpdateRequest.getTagId());
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<Tag> findById(Long tagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select tag_id, tag_name from tags where tag_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, tagId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbTagId = rs.getLong("tag_id");
                    String dbTagName = rs.getString("tag_name");
                    return Optional.of(Tag.ofExistingTag(dbTagId, dbTagName));
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Tag> findByTagName(String tagName) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select tag_id, tag_name from tags where tag_name=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setString(1, tagName);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbTagId = rs.getLong("tag_id");
                    String dbTagName = rs.getString("tag_name");
                    return Optional.of(Tag.ofExistingTag(dbTagId, dbTagName));
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public void deleteByTagId(Long tagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                delete from tags where tag_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, tagId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByTagName(String tagName) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from tags where tag_name=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setString(1, tagName);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean existsByTagId(Long tagId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
            select 1 from tags where tag_id=?
        """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, tagId);
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
    public boolean existsByTagName(String tagName) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select 1 from tags where tag_name=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setString(1,tagName);
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
