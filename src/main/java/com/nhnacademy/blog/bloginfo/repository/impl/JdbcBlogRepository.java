package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequestDto;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Repository(name = JdbcBlogRepository.BEAN_NAME)
public class JdbcBlogRepository implements BlogRepository {
    public static final String BEAN_NAME = "jdbcBlogRepository";

    @Override
    public int save(Blog blog) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    insert into blogs 
                    set
                        blog_name=?,
                        blog_mb_nickname=?,
                        blog_description=?,
                        created_at=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index=1;
            statement.setString(index++, blog.getBlogName());
            statement.setString(index++, blog.getBlogMbNickname());
            statement.setString(index++, blog.getBlogDescription());
            statement.setTimestamp(index++, Timestamp.valueOf(blog.getCreatedAt()));
            int rows = statement.executeUpdate();
            if(rows > 0) {
                try(ResultSet rs = statement.getGeneratedKeys()) {
                    if(rs.next()) {
                        long blogId = rs.getLong(1);
                        ReflectionUtils.setField(blog, "blogId", blogId);
                    }
                }
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int update(BlogUpdateRequestDto blogUpdateRequestDto) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    update blogs 
                    set
                        blog_name=?,
                        blog_mb_nickname=?,
                        blog_description=?,
                        updated_at=now()
                    where 
                        blog_id=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            int index=1;

            statement.setString(index++, blogUpdateRequestDto.getBlogName());
            statement.setString(index++, blogUpdateRequestDto.getBlogMbNickname());
            statement.setString(index++, blogUpdateRequestDto.getBlogDescription());
            statement.setLong(index++, blogUpdateRequestDto.getBlogId());

            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int delete(long blogId) {

        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from blogs where blog_id=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,blogId);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<Blog> findByBlogId(long blogId) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select 
                        blog_id, 
                        blog_name, 
                        blog_mb_nickname, 
                        blog_description, 
                        created_at, 
                        updated_at 
                    from 
                        blogs 
                    where 
                        blog_id=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,blogId);

            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()) {

                    long id = rs.getLong("blog_id");
                    String blogName = rs.getString("blog_name");
                    String blogMbNickname = rs.getString("blog_mb_nickname");
                    String blogDescription = rs.getString("blog_description");
                    LocalDateTime createdAt =  rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime updatedAt = null;
                    if(Objects.nonNull(rs.getTimestamp("updated_at"))){
                        updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
                    }

                    Blog blog = Blog.ofExistingBlogInfo(
                            id,
                            blogName,
                            blogMbNickname,
                            blogDescription,
                            createdAt,
                            updatedAt
                    );

                    return Optional.of(blog);
                }//end if

            }//end try
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public boolean existByBlogId(long blogId) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select 
                       1
                    from 
                        blogs 
                    where 
                        blog_id=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,blogId);

            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()) {
                    return true;
                }//end if
            }//end try
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
