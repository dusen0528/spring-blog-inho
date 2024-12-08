package com.nhnacademy.blog.category.repository.impl;

import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequestDto;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.common.websupport.PageRequest;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository(name = JdbcCategoryRepository.BEAN_NAME)
public class JdbcCategoryRepository implements CategoryRepository {
    public static final String BEAN_NAME = "jdbcCategoryRepository";

    @Override
    public int save(Category category) {
        Connection connection = DbConnectionThreadLocal.getConnection();

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

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            int index=1;

            if(Objects.nonNull(category.getCategoryPid())){
                psmt.setInt(index++, category.getCategoryPid());
            }else {
                psmt.setNull(index++, Types.INTEGER);
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

            int rows = psmt.executeUpdate();

            try(ResultSet rs = psmt.getGeneratedKeys()){
                if(rs.next()){
                    ReflectionUtils.setField(category,"categoryId", rs.getInt(1));
                }
            }

            return rows;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(CategoryUpdateRequestDto categoryUpdateRequestDto) {
        Connection connection = DbConnectionThreadLocal.getConnection();

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

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            int index=1;

            psmt.setInt(index++, categoryUpdateRequestDto.getCategoryPid());
            if(Objects.nonNull(categoryUpdateRequestDto.getTopicId())){
                psmt.setInt(index++, categoryUpdateRequestDto.getTopicId());
            }else {
                psmt.setNull(index++, Types.INTEGER);
            }
            psmt.setString(index++,categoryUpdateRequestDto.getCategoryName());
            psmt.setInt(index++,categoryUpdateRequestDto.getCategorySec());
            psmt.setLong(index++,categoryUpdateRequestDto.getCategoryId());

            return psmt.executeUpdate();

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(int categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = "delete from categories where category_id=?";
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setInt(1,categoryId);
            return psmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Category> findByCategoryId(int categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
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

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setInt(1,categoryId);

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    Integer dbCategoryId = rs.getInt("category_id");
                    Integer categoryPid = Objects.nonNull("category_pid") ? rs.getInt("category_pid") : null;
                    Long blogId = rs.getLong("blog_id");
                    Integer topicId = Objects.nonNull("topic_id") ? rs.getInt("topic_id") : null;
                    String categoryName = rs.getString("category_name");
                    Integer categorySec = rs.getInt("category_sec");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp("updated_at")) ?  rs.getTimestamp("updated_at").toLocalDateTime() : null;
                    Category category = Category.ofExistingCategory(dbCategoryId,categoryPid,blogId,topicId,categoryName,categorySec,createdAt,updatedAt);
                    return Optional.of(category);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Category> findAllByPageRequest(PageRequest pageRequest, Integer categoryPid) {

        Connection connection = DbConnectionThreadLocal.getConnection();
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
                    1
            """;

        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        if(Objects.nonNull(categoryPid)){
            sb.append(" and category_pid=? ");
        }
        sb.append(" order by category_sec asc ");
        sb.append(String.format(" limit %d, %d",pageRequest.getOffSet(), pageRequest.getSize()));

        List<Category> categories = new ArrayList<>();
        try (PreparedStatement psmt = connection.prepareStatement(sql)){
            if(Objects.nonNull(categoryPid)){
                psmt.setInt(1,categoryPid);
            }
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    Integer dbCategoryId = rs.getInt("category_id");
                    Integer dbCategoryPid = rs.getInt("category_pid");
                    Long blogId = rs.getLong("blog_id");
                    Integer topicId = rs.getInt("topic_id");
                    String categoryName = rs.getString("category_name");
                    Integer categorySec = rs.getInt("category_sec");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                    Category category = Category.ofExistingCategory(dbCategoryId,dbCategoryPid,blogId,topicId,categoryName,categorySec,createdAt,updatedAt);
                    categories.add(category);
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        return categories;
    }

    @Override
    public long count(Integer categoryPid) {

        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = "select count(category_id) from categories where 1";
        StringBuilder sb = new StringBuilder(sql);
        if(Objects.nonNull(categoryPid)){
            sb.append(" and category_pid=? ");
        }

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            if(Objects.nonNull(categoryPid)){
                psmt.setInt(1,categoryPid);
            }
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    return rs.getLong(1);
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public boolean existsByCategoryId(int categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = "select 1 from categories where category_id=?";

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setInt(1,categoryId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    return true;
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return false;
    }
}
