package com.nhnacademy.blog.bloginfo.repository.impl;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.dto.BlogResponse;
import com.nhnacademy.blog.bloginfo.dto.BlogUpdateRequest;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository(name = JdbcBlogRepository.BEAN_NAME)
public class JdbcBlogRepository implements BlogRepository {
    public static final String BEAN_NAME = "jdbcBlogRepository";

    @Override
    public void save(Blog blog) {
        Connection connection = DbConnectionThreadLocal.getConnection();

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

        try(PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            int index=1;
            statement.setString(index++,blog.getBlogFid());
            statement.setBoolean(index++, blog.isBlogMain());
            statement.setString(index++, blog.getBlogName());
            statement.setString(index++, blog.getBlogMbNickname());
            statement.setString(index++, blog.getBlogDescription());
            statement.setBoolean(index++, blog.getBlogIsPublic());
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
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(BlogUpdateRequest blogUpdateRequest) {

        Connection connection = DbConnectionThreadLocal.getConnection();

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

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            int index=1;
            statement.setBoolean(index++, blogUpdateRequest.isBlogMain());
            statement.setString(index++, blogUpdateRequest.getBlogName());
            statement.setString(index++, blogUpdateRequest.getBlogMbNickname());
            statement.setString(index++, blogUpdateRequest.getBlogDescription());
            statement.setLong(index++, blogUpdateRequest.getBlogId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByBlogId(long blogId) {

        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from blogs where blog_id=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,blogId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public Optional<Blog> findByBlogId(long blogId) {

        Connection connection = DbConnectionThreadLocal.getConnection();

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

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1,blogId);

            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()) {

                    long id = rs.getLong("blog_id");
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

                    Blog blog = Blog.ofExistingBlogInfo(
                            id,
                            blogFid,
                            blogMain,
                            blogName,
                            blogMbNickname,
                            blogDescription,
                            blogIsPublic,
                            createdAt,
                            updatedAt
                    );

                    return Optional.of(blog);
                }//end if

            }//end try
        } catch (SQLException e) {
            throw new DatabaseException(e);
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
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existByBlogFid(String blogFid) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select 
                       1
                    from 
                        blogs 
                    where 
                        blog_fid=?
                """;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1,blogFid);

            try(ResultSet rs = statement.executeQuery()){
                if(rs.next()) {
                    return true;
                }//end if
            }//end try
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public boolean existMainBlogByMbNo(long mbNo) {

        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    SELECT 
                        count(*)
                    FROM members a
                        INNER JOIN blog_members_mapping b ON a.mb_no = b.mb_no
                        INNER JOIN blogs c ON b.blog_id = c.blog_id
                    WHERE 
                             a.mb_no=? 
                         and a.blog_is_main=1;
                         and b.role_id='ROLE_OWNER';
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,mbNo);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    return true;
                }
            }
        }catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return false;
    }

    @Override
    public void updateByBlogIsPublic(long blogId, boolean blogIsPublic) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                update blogs 
                set 
                    blog_is_public=? 
                where blog_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setBoolean(index++,blogIsPublic);
            psmt.setLong(index++,blogId);
            psmt.executeUpdate();
        }catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void updateBlogMain(long blogId, boolean isBlogMain) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    update blogs 
                    set
                        blog_main=?
                    where 
                            blog_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setBoolean(index++,isBlogMain);
            psmt.setLong(index++,blogId);
            psmt.executeUpdate();
        }catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public long countByMbNo(long mbNo, String roleId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    select
                    	count(*)
                    from
                    	blog_members_mapping a
                        left join blogs b on a.blog_id = b.blog_id
                    where
                    	a.blog_id=b.blog_id
                      and a.mb_no=?  
                      and a.role_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setLong(index++,mbNo);
            psmt.setString(index++,roleId);

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    return rs.getLong(1);
                }
            }
        }catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return 0;
    }

    @Override
    public List<BlogResponse> findAllBlogs(long mbNo, String roleId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

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
                	blog_members_mapping a
                    left join blogs b on a.blog_id = b.blog_id
                where
                	a.blog_id=b.blog_id
                    and a.mb_no=?
                    and a.role_id=?
                """;

        List<BlogResponse> blogResponseList = new ArrayList<>();

        try(PreparedStatement psmt = connection.prepareStatement(sql)){

            psmt.setLong(1,mbNo);
            psmt.setString(2,roleId);

            try(ResultSet rs = psmt.executeQuery()){

                while(rs.next()) {

                    Long blogId = rs.getLong("blog_id");
                    String blogFid = rs.getString("blog_fid");
                    boolean blogMain = rs.getBoolean("blog_main");
                    String blogName = rs.getString("blog_name");
                    String blogMbNickname = rs.getString("blog_mb_nickname");
                    String blogDescription = rs.getString("blog_description");
                    Boolean blogIsPublic = rs.getBoolean("blog_is_public");
                    LocalDateTime createdAt = Objects.nonNull(rs.getObject("created_at")) ? rs.getTimestamp("created_at").toLocalDateTime() : null;
                    LocalDateTime updatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

                    BlogResponse blogResponse = new BlogResponse(
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
                    blogResponseList.add(blogResponse);
                }
            }

        }catch (SQLException e) {
            throw new DatabaseException(e);
        }

        return blogResponseList;
    }

}
