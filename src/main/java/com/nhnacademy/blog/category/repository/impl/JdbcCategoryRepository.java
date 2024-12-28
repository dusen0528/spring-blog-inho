package com.nhnacademy.blog.category.repository.impl;

import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryResponse;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequest;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("java:S1192")

//@Component
public class JdbcCategoryRepository implements CategoryRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcCategoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void save(Category category) {
        String sql = """
                    insert into categories
                    set
                        category_pid=?,
                        blog_id=?,
                        topic_id=?,
                        category_name=?,
                        category_sec=?,
                        created_at=?
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection->{
                PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                int index=1;

                if(Objects.nonNull(category.getCategoryPid())){
                    psmt.setLong(index++, category.getCategoryPid());
                }else {
                    psmt.setNull(index++, Types.BIGINT);
                }

                psmt.setLong(index++,category.getBlogId());
                if(Objects.nonNull(category.getTopicId())){
                    psmt.setInt(index++, category.getTopicId());
                }else {
                    psmt.setNull(index++, Types.INTEGER);
                }

                psmt.setString(index++,category.getCategoryName());
                psmt.setInt(index++,category.getCategorySec());
                psmt.setTimestamp(index++, Timestamp.valueOf(category.getCreatedAt()));

                return psmt;
            },keyHolder);

        if(rows>0){
            ReflectionUtils.setField(category, "categoryId", Objects.requireNonNull(keyHolder.getKey()).longValue());
        }
    }

    @Override
    public void update(CategoryUpdateRequest categoryUpdateRequest) {
        String sql = """
                    update categories
                    set 
                        category_pid=?,
                        topic_id=?,
                        category_name=?,
                        category_sec=?,
                        updated_at=now()
                    where category_id=?
                """;

        jdbcTemplate.update(connection->{
            int index=1;
            PreparedStatement psmt = connection.prepareStatement(sql);
            if(Objects.nonNull(categoryUpdateRequest.getCategoryPid())){
                psmt.setLong(index++, categoryUpdateRequest.getCategoryPid());
            }else{
                psmt.setNull(index++, Types.BIGINT);
            }

            if(Objects.nonNull(categoryUpdateRequest.getTopicId())){
                psmt.setInt(index++, categoryUpdateRequest.getTopicId());
            }else {
                psmt.setNull(index++, Types.INTEGER);
            }

            psmt.setString(index++, categoryUpdateRequest.getCategoryName());
            psmt.setInt(index++, categoryUpdateRequest.getCategorySec());
            psmt.setLong(index++, categoryUpdateRequest.getCategoryId());
            return psmt;
        });
    }

    @Override
    public void deleteByCategoryId(Long categoryId) {
        String sql = "delete from categories where category_id=?";
        jdbcTemplate.update(sql, categoryId);
    }


    @Override
    public Optional<Category> findByCategoryId(Long categoryId) {
        String sql = """
                select
                    category_id,
                    category_pid,
                    blog_id,
                    topic_id,
                    category_name,
                    category_sec,
                    created_at,
                    updated_at
                from 
                    categories
                where 
                    category_id=?
                """;
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(sql,new CategoryRowMapper(),categoryId)
        );
    }

    @Override
    public List<Category> findAll(Long blogId, Long categoryPid) {
        String sql = """
                select
                    category_id,
                    category_pid,
                    blog_id,
                    topic_id,
                    category_name,
                    category_sec,
                    created_at,
                    updated_at
                from
                    categories
                where
                    blog_id=?
            """;

        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        if(Objects.nonNull(categoryPid)){
            sb.append(" and category_pid=? ");
        }
        sb.append(" order by category_sec asc ");
        if(Objects.isNull(categoryPid)){
            return jdbcTemplate.query(sb.toString(),new CategoryRowMapper(), blogId);
        }
        return jdbcTemplate.query(sb.toString(),new CategoryRowMapper(), blogId, categoryPid);
    }

    @Override
    public List<CategoryResponse> findAllByBlogId(Long blogId) {

        String sql = """
                select
                    category_id,
                    category_pid,
                    blog_id,
                    topic_id,
                    category_name,
                    category_sec
                from
                    categories
                where 
                    blog_id=?
                order by category_sec asc
            """;

        return jdbcTemplate.query(sql,new CategoryResponseRowMapper(),blogId);
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        String sql = "select 1 from categories where category_id=?";
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, categoryId));
    }

    @Override
    public boolean existsSubCategoryByCategoryId(Long categoryId) {
        String sql = "select 1 from categories where category_pid = ? ";
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, categoryId));
    }

    private static class CategoryRowMapper implements RowMapper<Category> {
        @Override
        public Category mapRow(ResultSet rs, int rowNum) throws SQLException {

            Long dbCategoryId = rs.getLong("category_id");
            Long categoryPid = Objects.nonNull(rs.getObject("category_pid")) ? rs.getLong("category_pid") : null;
            Long blogId = rs.getLong("blog_id");
            Integer topicId = Objects.nonNull(rs.getObject("topic_id")) ? rs.getInt("topic_id") : null;
            String categoryName = rs.getString("category_name");
            Integer categorySec = rs.getInt("category_sec");
            LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
            LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp("updated_at")) ?  rs.getTimestamp("updated_at").toLocalDateTime() : null;

            return Category.ofExistingCategory(
                            dbCategoryId,
                            categoryPid,
                            blogId,
                            topicId,
                            categoryName,
                            categorySec,
                            createdAt,
                            updatedAt
            );
        }
    }//end inner class

    private static class CategoryResponseRowMapper implements RowMapper<CategoryResponse> {

        @Override
        public CategoryResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long dbCategoryId = rs.getLong("category_id");
            Long dbCategoryPid = rs.getLong("category_pid");
            Long dbBlogId = rs.getLong("blog_id");
            Integer topicId = rs.getInt("topic_id");
            String categoryName = rs.getString("category_name");
            Integer categorySec = rs.getInt("category_sec");

            return new CategoryResponse(
                    dbCategoryId,
                    dbCategoryPid,
                    dbBlogId,
                    topicId,
                    categoryName,
                    categorySec
            );
        }
    }
}
