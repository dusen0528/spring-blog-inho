package com.nhnacademy.blog.postcategory.repository.impl;

import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.postcategory.domain.PostCategory;
import com.nhnacademy.blog.postcategory.repository.PostCategoryRepository;

import java.sql.*;
import java.util.Optional;

@Repository(JdbcPostCategoryRepository.BEAN_NAME)
public class JdbcPostCategoryRepository implements PostCategoryRepository {
    public static final String BEAN_NAME = "jdbcPostCategoryRepository";

    @Override
    public void save(PostCategory postCategory) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                insert into post_category_mappings
                set 
                    post_id = ?,
                    category_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index = 1;
            psmt.setLong(index++, postCategory.getPostId());
            psmt.setLong(index++, postCategory.getCategoryId());

            psmt.executeUpdate();

            try(ResultSet rs = psmt.getGeneratedKeys()) {
                if(rs.next()) {
                    long postCategoryId =  rs.getLong(1);
                    ReflectionUtils.setField(postCategory,"postCategoryId", postCategoryId);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByPostCategoryId(Long postCategoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from post_category_mappings where post_category_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postCategoryId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByPostIdAndCategoryId(Long postId, Long categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from post_category_mappings where post_id = ? and category_id= ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.setLong(2, categoryId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<PostCategory> findByPostCategoryId(Long postCategoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select 
                    post_category_id,
                    post_id, 
                    category_id 
                from 
                    post_category_mappings 
                where 
                    post_category_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postCategoryId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbPostCategoryId = rs.getLong("post_category_id");
                    long dbPostId = rs.getLong("post_id");
                    long dbCategoryId = rs.getLong("category_id");
                    PostCategory postCategory = PostCategory.ofExistingPostCategory(dbPostCategoryId,dbPostId,dbCategoryId);
                    return Optional.of(postCategory);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<PostCategory> findByPostIdAndCategoryId(Long postId, Long categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select 
                    post_category_id,
                    post_id, 
                    category_id 
                from 
                    post_category_mappings 
                where 
                        post_id = ?
                    and category_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.setLong(2, categoryId);

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbPostCategoryId = rs.getLong("post_category_id");
                    long dbPostId = rs.getLong("post_id");
                    long dbCategoryId = rs.getLong("category_id");
                    PostCategory postCategory = PostCategory.ofExistingPostCategory(dbPostCategoryId,dbPostId,dbCategoryId);
                    return Optional.of(postCategory);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByPostCategoryId(Long postCategoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select 
                   1
                from 
                    post_category_mappings 
                where 
                    post_category_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postCategoryId);
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
    public boolean existsByPostIdAndCategoryId(Long postId, Long categoryId) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select
                    1
                from 
                    post_category_mappings 
                where 
                        post_id = ?
                    and category_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.setLong(2, categoryId);

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
