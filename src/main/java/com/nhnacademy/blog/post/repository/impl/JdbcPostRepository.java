package com.nhnacademy.blog.post.repository.impl;

import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.page.PageImpl;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.dto.PostResponse;
import com.nhnacademy.blog.post.dto.PostSearchRequest;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;
import com.nhnacademy.blog.post.repository.PostRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository(JdbcPostRepository.BEAN_NAME)
public class JdbcPostRepository implements PostRepository {
    public static final String BEAN_NAME = "jdbcPostRepository";

    @Override
    public void save(Post post) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    insert into posts set 
                            blog_id = ?,
                            created_mb_no = ?,
                            post_title = ?,
                            post_content = ?,
                            post_is_public = ?,
                            created_at = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            int index=1;
            psmt.setLong(index++, post.getBlogId());
            psmt.setLong(index++, post.getCreatedMbNo());
            psmt.setString(index++, post.getPostTitle());
            psmt.setString(index++, post.getPostContent());
            psmt.setBoolean(index++, post.isPostIsPublic());
            psmt.setTimestamp(index++, Timestamp.valueOf(post.getCreatedAt()));
            psmt.executeUpdate();
            try(ResultSet rs = psmt.getGeneratedKeys()) {
                if(rs.next()) {
                    ReflectionUtils.setField(post, "postId", rs.getLong(1));
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(PostUpdateRequest postUpdateRequest) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                update posts set
                    post_title = ?,
                    post_content = ?,
                    post_is_public = ?,
                    updated_at = ?,
                where
                    post_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setString(index++, postUpdateRequest.getPostTitle());
            psmt.setString(index++, postUpdateRequest.getPostContent());
            psmt.setBoolean(index++, postUpdateRequest.isPostIsPublic());
            psmt.setTimestamp(index++, Timestamp.valueOf(LocalDateTime.now()));
            psmt.setLong(index++, postUpdateRequest.getPostId());
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByPostId(Long postId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                delete from posts where post_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void updateByPostIdAndPostIsPublic(Long postId, Boolean isPublic) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    update posts set 
                        post_is_public = ? 
                    where 
                        post_id = ?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setBoolean(index++, isPublic);
            psmt.setLong(index++, postId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public boolean existsByPostId(Long postId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select 1 from posts where post_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
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
    public Optional<Post> findByPostId(Long postId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select
                  post_id,
                  blog_id,
                  created_mb_no,
                  updated_mb_no,
                  post_title,
                  post_content,
                  post_is_public,
                  created_at,
                  updated_at
                from posts where post_id = ?
            """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1, postId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()) {
                    long dbPostId = rs.getLong("post_id");
                    long dbBlogId = rs.getLong("blog_id");
                    long dbCreatedMbNo = rs.getLong("created_mb_no");
                    long dbUpdatedMbNo = rs.getLong("updated_mb_no");
                    String dbPostTitle = rs.getString("post_title");
                    String dbPostContent = rs.getString("post_content");
                    boolean dbPostIsPublic = rs.getBoolean("post_is_public");
                    LocalDateTime dbCreatedAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime dbUpdatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

                    Post post = Post.ofExistingPost(
                            dbPostId,
                            dbBlogId,
                            dbCreatedMbNo,
                            dbUpdatedMbNo,
                            dbPostTitle,
                            dbPostContent,
                            dbPostIsPublic,
                            dbCreatedAt,
                            dbUpdatedAt
                    );
                    return Optional.of(post);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public Page<PostResponse> findAllByPageableAndPostSearchRequest(Pageable pageable, PostSearchRequest searchRequest) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """                    
                    select
                          a.post_id,
                          a.blog_id,
                          a.created_mb_no,
                          a.updated_mb_no,
                          (select mb_name from members where mb_no=a.created_mb_no) as created_mb_name,
                          (select mb_name from members where mb_no=a.updated_mb_no) as updated_mb_name,
                          a.post_title,
                          a.post_content,
                          a.post_is_public,
                          a.created_at,
                          a.updated_at
                    from posts a 
                        left join post_categories_mapping b on a.post_id= b.post_id
                        
                    where
                            a.blog_id = ?
                        and a.post_is_public = ?
                        and b.category_id = ?
                    
                    order by a.post_id desc
                    
                    limit ? ,?
                """;

        List<PostResponse> postResponseList = new ArrayList<>();

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setLong(index++, searchRequest.getBlogId());
            psmt.setBoolean(index++, searchRequest.isPostIsPublic());
            psmt.setLong(index++, searchRequest.getCategoryId() );
            psmt.setLong(index++, pageable.getOffset());
            psmt.setLong(index++, pageable.getPageSize());

            try(ResultSet rs = psmt.executeQuery()){
                while (rs.next()) {

                    long dbPostId = rs.getLong("post_id");
                    long dbBlogId = rs.getLong("blog_id");
                    long dbCreatedMbNo = rs.getLong("created_mb_no");
                    String dbCreatedMbName = rs.getString("created_mb_name");
                    long dbUpdatedMbNo = rs.getLong("updated_mb_no");
                    String dbUpdatedMbName = rs.getString("updated_mb_name");
                    String dbPostTitle = rs.getString("post_title");
                    String dbPostContent = rs.getString("post_content");
                    boolean dbPostIsPublic = rs.getBoolean("post_is_public");
                    LocalDateTime dbCreatedAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime dbUpdatedAt = Objects.nonNull("updated_at") ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

                    PostResponse postResponse = new PostResponse(
                        dbPostId,
                        dbBlogId,
                        dbCreatedMbNo,
                        dbCreatedMbName,
                        dbUpdatedMbNo,
                        dbUpdatedMbName,
                        dbPostTitle,
                        dbPostContent,
                        dbPostIsPublic,
                        dbCreatedAt,
                        dbUpdatedAt
                    );

                    postResponseList.add(postResponse);
                }
            }

        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return  new PageImpl<>(postResponseList, pageable.getPageNumber(),pageable.getPageSize(),totalRowsByPostSearchRequest(searchRequest));
    }

    @Override
    public Long totalRowsByPostSearchRequest(PostSearchRequest postSearchRequest) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """                    
                    select
                          count(*)
                    from posts a 
                        left join post_categories_mapping b on a.post_id= b.post_id
                        
                    where
                            a.blog_id = ?
                        and a.post_is_public = ?
                        and b.category_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){

            int index=1;
            psmt.setLong(index++, postSearchRequest.getBlogId());
            psmt.setBoolean(index++, postSearchRequest.isPostIsPublic());
            psmt.setLong(index++, postSearchRequest.getCategoryId());

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    return rs.getLong(index);
                }
            }

        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return 0L;
    }
}
