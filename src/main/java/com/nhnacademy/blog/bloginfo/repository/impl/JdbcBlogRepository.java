package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
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

//@Component
public class JdbcBlogRepository implements BlogRepository {


    private final JdbcTemplate jdbcTemplate;

    public JdbcBlogRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void save(Blog blog) {

        String sql = """
                    insert into blogs
                    set
                        blog_fid=?,
                        blog_main=?,
                        blog_name=?,
                        blog_mb_nickname=?,
                        blog_description=?,
                        blog_is_public=?,
                        created_at=?
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int rows = jdbcTemplate.update(connection->{
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                int index=1;
                statement.setString(index++,blog.getBlogFid());
                statement.setBoolean(index++, blog.isBlogMain());
                statement.setString(index++, blog.getBlogName());
                statement.setString(index++, blog.getBlogMbNickname());
                statement.setString(index++, blog.getBlogDescription());
                statement.setBoolean(index++, blog.getBlogIsPublic());
                statement.setTimestamp(index++, Timestamp.valueOf(blog.getCreatedAt()));
            return statement;
        },keyHolder);

        if(rows>0) {
            ReflectionUtils.setField(blog, "blogId", Objects.requireNonNull(keyHolder.getKey()).longValue());
        }

    }


    @Override
    public void update(BlogUpdateRequest blogUpdateRequest) {
        String sql = """
                    update blogs 
                    set
                        blog_main=?,
                        blog_name=?,
                        blog_mb_nickname=?,
                        blog_description=?,
                        updated_at=now()
                    where 
                        blog_id=?
                """;
        jdbcTemplate.update(connection->{
            PreparedStatement statement = connection.prepareStatement(sql);
            int index=1;
            statement.setBoolean(index++, blogUpdateRequest.isBlogMain());
            statement.setString(index++, blogUpdateRequest.getBlogName());
            statement.setString(index++, blogUpdateRequest.getBlogMbNickname());
            statement.setString(index++, blogUpdateRequest.getBlogDescription());
            statement.setLong(index++, blogUpdateRequest.getBlogId());
            return statement;
        });
    }


    @Override
    public void deleteByBlogId(long blogId) {

        String sql = """
                    delete from blogs where blog_id=?
                """;

        jdbcTemplate.update(sql,blogId);
    }

    @Override
    public Optional<Blog> findByBlogId(long blogId) {

        String sql = """
                    select
                        blog_id,
                        blog_fid,
                        blog_main,
                        blog_name,
                        blog_mb_nickname,
                        blog_description,
                        blog_is_public,
                        created_at,
                        updated_at
                    from
                        blogs
                    where
                        blog_id=?
                """;

        return Optional.of(jdbcTemplate.queryForObject(sql, new BlogRowMapper(), blogId));
    }

    @Override
    public boolean existByBlogId(long blogId) {

        String sql = """
                    select 
                       1
                    from 
                        blogs 
                    where 
                        blog_id=?
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, blogId));
    }

    @Override
    public boolean existByBlogFid(String blogFid) {

        String sql = """
                    select 
                       1
                    from 
                        blogs 
                    where 
                        blog_fid=?
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, blogFid));
    }

    @Override
    public boolean existMainBlogByMbNo(long mbNo) {

        String sql = """
                    SELECT 
                        1
                    FROM members a
                        INNER JOIN blog_member_mappings b ON a.mb_no = b.mb_no
                        INNER JOIN blogs c ON b.blog_id = c.blog_id
                    WHERE 
                             a.mb_no=? 
                         and c.blog_main=1
                         and b.role_id='ROLE_OWNER'
                """;
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, mbNo));
    }


    @Override
    public void updateByBlogIsPublic(long blogId, boolean blogIsPublic) {
        String sql = """
                update blogs 
                set 
                    blog_is_public=? 
                where blog_id=?
                """;

        jdbcTemplate.update(sql,blogIsPublic,blogId);
    }


    @Override
    public void updateBlogMain(long blogId, boolean isBlogMain) {
        String sql = """
                    update blogs 
                    set
                        blog_main=?
                    where 
                            blog_id = ?
                """;
        jdbcTemplate.update(sql,isBlogMain,blogId);

    }

    @Override
    public long countByMbNo(long mbNo, String roleId) {

        String sql = """
            select
                count(*)
            from
                blog_member_mappings a
            left join blogs b on a.blog_id = b.blog_id
            where
                        a.blog_id=b.blog_id
                    and a.mb_no=?  
                    and a.role_id=?
        """;

        Long count = jdbcTemplate.queryForObject(sql,Long.class,mbNo, roleId);
        return Objects.isNull(count) ? 0 : count;

    }

    @Override
    public List<BlogResponse> findAllBlogs(long mbNo, String roleId) {

        String sql = """
                        select
                            b.blog_id,
                            b.blog_fid,
                            b.blog_main,
                            b.blog_name,
                            b.blog_mb_nickname,
                            b.blog_description,
                            b.blog_is_public,
                            b.created_at,
                            b.updated_at
                        from
                            blog_member_mappings a
                            left join blogs b on a.blog_id = b.blog_id
                        where
                                a.blog_id=b.blog_id
                            and a.mb_no=?
                            and a.role_id=?
        """;

        return jdbcTemplate.query(sql, new BlogResponseRowMapper(),mbNo, roleId);
    }

    private static class BlogRowMapper implements RowMapper<Blog> {

        @Override
        public Blog mapRow(ResultSet rs, int rowNum) throws SQLException {

            long blogId = rs.getLong("blog_id");
            String blogFid = rs.getString("blog_fid");
            Boolean blogMain = rs.getBoolean("blog_main");
            String blogName = rs.getString("blog_name");
            String blogMbNickname = rs.getString("blog_mb_nickname");
            String blogDescription = rs.getString("blog_description");
            Boolean blogIsPublic = rs.getBoolean("blog_is_public");
            LocalDateTime createdAt =  rs.getTimestamp("created_at").toLocalDateTime();
            LocalDateTime updatedAt = null;
            if(Objects.nonNull(rs.getTimestamp("updated_at"))){
                updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();
            }

            return Blog.ofExistingBlogInfo(
                    blogId,
                    blogFid,
                    blogMain,
                    blogName,
                    blogMbNickname,
                    blogDescription,
                    blogIsPublic,
                    createdAt,
                    updatedAt
            );

        }
    }

    private static class BlogResponseRowMapper implements RowMapper<BlogResponse> {

        @Override
        public BlogResponse mapRow(ResultSet rs, int rowNum) throws SQLException {

            Long blogId = rs.getLong("blog_id");
            String blogFid = rs.getString("blog_fid");
            boolean blogMain = rs.getBoolean("blog_main");
            String blogName = rs.getString("blog_name");
            String blogMbNickname = rs.getString("blog_mb_nickname");
            String blogDescription = rs.getString("blog_description");
            Boolean blogIsPublic = rs.getBoolean("blog_is_public");
            LocalDateTime createdAt = Objects.nonNull(rs.getObject("created_at")) ? rs.getTimestamp("created_at").toLocalDateTime() : null;
            LocalDateTime updatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

            return new BlogResponse(
                    blogId,
                    blogFid,
                    blogMain,
                    blogName,
                    blogMbNickname,
                    blogDescription,
                    blogIsPublic,
                    createdAt,
                    updatedAt
            );
        }
    }

}
