package com.nhnacademy.blog.category.repository.impl;

import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.dto.CategoryResponse;
import com.nhnacademy.blog.category.dto.CategoryUpdateRequest;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("java:S1192")
@Repository
public class JdbcCategoryRepository implements CategoryRepository {

    @Override
    public void save(Category category) {
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

            psmt.executeUpdate();

            try(ResultSet rs = psmt.getGeneratedKeys()){
                if(rs.next()){
                    ReflectionUtils.setField(category,"categoryId", rs.getLong(1));
                }
            }

        }catch (SQLException e){
            throw new DatabaseException(e);
        }

    }

    @Override
    public void update(CategoryUpdateRequest categoryUpdateRequest) {
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

            psmt.executeUpdate();

        }catch (SQLException e){
            throw new DatabaseException(e);
        }

    }

    @Override
    public void deleteByCategoryId(Long categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = "delete from categories where category_id=?";
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,categoryId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<Category> findByCategoryId(Long categoryId) {
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
            psmt.setLong(1,categoryId);

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    Long dbCategoryId = rs.getLong("category_id");
                    Long categoryPid = Objects.nonNull(rs.getObject("category_pid")) ? rs.getLong("category_pid") : null;
                    Long blogId = rs.getLong("blog_id");
                    Integer topicId = Objects.nonNull(rs.getObject("topic_id")) ? rs.getInt("topic_id") : null;
                    String categoryName = rs.getString("category_name");
                    Integer categorySec = rs.getInt("category_sec");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp("updated_at")) ?  rs.getTimestamp("updated_at").toLocalDateTime() : null;
                    Category category = Category.ofExistingCategory(dbCategoryId,categoryPid,blogId,topicId,categoryName,categorySec,createdAt,updatedAt);
                    return Optional.of(category);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }

    @Override
    public List<Category> findAll(Long blogId, Long categoryPid) {

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
                    blog_id=?
            """;

        StringBuilder sb = new StringBuilder();
        sb.append(sql);
        if(Objects.nonNull(categoryPid)){
            sb.append(" and category_pid=? ");
        }
        sb.append(" order by category_sec asc ");

        List<Category> categories = new ArrayList<>();
        try (PreparedStatement psmt = connection.prepareStatement(sb.toString())){
            psmt.setLong(1,blogId);

            if(Objects.nonNull(categoryPid)){
                psmt.setLong(2,categoryPid);
            }

            try(ResultSet rs = psmt.executeQuery()){
                while(rs.next()){
                    Long dbCategoryId = rs.getLong("category_id");
                    Long dbCategoryPid = rs.getLong("category_pid");
                    Long dbBlogId = rs.getLong("blog_id");
                    Integer topicId = rs.getInt("topic_id");
                    String categoryName = rs.getString("category_name");
                    Integer categorySec = rs.getInt("category_sec");
                    LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updatedAt = Objects.nonNull(rs.getTimestamp("updated_at")) ?  rs.getTimestamp("updated_at").toLocalDateTime() : null;
                    Category category = Category.ofExistingCategory(dbCategoryId,dbCategoryPid,dbBlogId,topicId,categoryName,categorySec,createdAt,updatedAt);
                    categories.add(category);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return categories;
    }

    @Override
    public List<CategoryResponse> findAllByBlogId(Long blogId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
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

        List<CategoryResponse> categoryResponseList = new ArrayList<>();
        try (PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,blogId);

            try(ResultSet rs = psmt.executeQuery()){
                while(rs.next()){
                    Long dbCategoryId = rs.getLong("category_id");
                    Long dbCategoryPid = rs.getLong("category_pid");
                    Long dbBlogId = rs.getLong("blog_id");
                    Integer topicId = rs.getInt("topic_id");
                    String categoryName = rs.getString("category_name");
                    Integer categorySec = rs.getInt("category_sec");
                    CategoryResponse categoryResponse = new CategoryResponse(dbCategoryId,dbCategoryPid,dbBlogId,topicId,categoryName,categorySec);
                    categoryResponseList.add(categoryResponse);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return categoryResponseList;
    }

    @Override
    public boolean existsByCategoryId(Long categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = "select 1 from categories where category_id=?";

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,categoryId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    return true;
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existsSubCategoryByCategoryId(Long categoryId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = "select 1 from categories where category_pid = ? ";
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,categoryId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    return true;
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return false;
    }
}
