package com.nhnacademy.blog.post.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.common.websupport.page.Page;
import com.nhnacademy.blog.common.websupport.page.PageImpl;
import com.nhnacademy.blog.common.websupport.pageable.Pageable;
import com.nhnacademy.blog.post.domain.Post;
import com.nhnacademy.blog.post.dto.PostResponse;
import com.nhnacademy.blog.post.dto.PostSearchParam;
import com.nhnacademy.blog.post.dto.PostUpdateRequest;
import com.nhnacademy.blog.post.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Slf4j
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
                    updated_at = ?
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
    public Page<PostResponse> findAllByPageableAndPostSearchRequest(Pageable pageable, PostSearchParam searchRequest) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        StringBuilder sb = new StringBuilder();

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
                          a.updated_at,
                          
                          concat('[',
                              group_concat(
                                JSON_OBJECT(
                                    'categoryId',ifnull(c.category_id,-1), 
                                    'categoryName',ifnull(c.category_name,''),
                                    'topicId', ifnull(d.topic_id,-1),
                                    'topicName',ifnull(d.topic_name,'')
                                ) SEPARATOR ','
                              )
                           ,']')as category_info,
                           concat(
                            '[',
                                group_concat(
                                    JSON_OBJECT(
                                        'tagId', ifnull(f.tag_id,-1),
                                        'tagName', ifnull(f.tag_name,'')
                                    )
                                ),
                            ']'
                           ) as tag_info
                    from posts a 
                        left join post_category_mappings b on a.post_id= b.post_id
                        left join categories c on c.category_id = b.category_id
                        left join topics d on d.topic_id = c.topic_id
                        left join post_tag_mappings e on e.post_id =  a.post_id
                        left join tags f on f.tag_id = e.tag_id
                    where
                            a.blog_id = ?
                        
                """;
        sb.append(sql);
        if(Objects.nonNull(searchRequest.isPostIsPublic())){
            sb.append(" and a.post_is_public = ? ");
        }

        if(Objects.nonNull(searchRequest.getCategoryId())){
            sb.append(" and c.category_id = ? ");
        }

        sb.append("""
                    
                    group by a.post_id
                    order by a.post_id desc
                    limit ? ,?
                     
                """);

        List<PostResponse> postResponseList = new ArrayList<>();

        try(PreparedStatement psmt = connection.prepareStatement(sb.toString())){
            int index=1;

            psmt.setLong(index++, searchRequest.getBlogId());

            if(Objects.nonNull(searchRequest.isPostIsPublic())){
                psmt.setBoolean(index++, searchRequest.isPostIsPublic());
            }

            if(Objects.nonNull(searchRequest.getCategoryId())) {
                psmt.setLong(index++, searchRequest.getCategoryId() );
            }

            psmt.setLong(index++, pageable.getOffset());
            psmt.setLong(index++, pageable.getPageSize());

            try(ResultSet rs = psmt.executeQuery()){
                while (rs.next()) {

                    long dbPostId = rs.getLong("post_id");
                    long dbBlogId = rs.getLong("blog_id");
                    long dbCreatedMbNo = rs.getLong("created_mb_no");
                    String dbCreatedMbName = rs.getString("created_mb_name");
                    Long dbUpdatedMbNo = Objects.nonNull(rs.getObject("updated_mb_no")) ?  rs.getLong("updated_mb_no") : null;
                    String dbUpdatedMbName = rs.getString("updated_mb_name");
                    String dbPostTitle = rs.getString("post_title");
                    String dbPostContent = rs.getString("post_content");
                    boolean dbPostIsPublic = rs.getBoolean("post_is_public");
                    LocalDateTime dbCreatedAt = rs.getTimestamp("created_at").toLocalDateTime();
                    LocalDateTime dbUpdatedAt = Objects.nonNull(rs.getObject("updated_at")) ? rs.getTimestamp("updated_at").toLocalDateTime() : null;

                    String categoryInfo = rs.getString("category_info");
                    String tagInfo = rs.getString("tag_info");

                    //categoryInfo json -> list 변환
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<PostResponse.CategoryInfo> categoryInfoList;

                    try {
                        categoryInfoList = objectMapper.readValue(categoryInfo, new TypeReference<List<PostResponse.CategoryInfo>>(){});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    List<PostResponse.TagInfo> tagInfoList;
                    try {
                        tagInfoList = objectMapper.readValue(tagInfo, new TypeReference<List<PostResponse.TagInfo>>(){});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

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
                        dbUpdatedAt,
                        categoryInfoList,
                        tagInfoList
                    );

                    log.debug("category_info: {}", categoryInfo);
                    log.debug("tag_info: {}", tagInfo);
                    log.debug("postResponse: {}", postResponse);
                    postResponseList.add(postResponse);
                }
            }

        }catch (SQLException e){
            throw new DatabaseException(e);
        }
        long totalRows = totalRowsByPostSearchRequest(pageable,searchRequest);
        log.debug("searchRequest: {}", searchRequest);
        log.debug("pageable: {}", pageable);
        log.debug("totalRows: {}", totalRows);

        return  new PageImpl<>(postResponseList, pageable.getPageNumber(),pageable.getPageSize(),totalRows);
    }

    @Override
    public Long totalRowsByPostSearchRequest(Pageable pageable, PostSearchParam postSearchParam) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        StringBuilder sb = new StringBuilder();
        String sql = """                    
                    select
                              count(distinct a.post_id)
                        from posts a
                            left join post_category_mappings b on a.post_id= b.post_id
                        where
                                a.blog_id = ?
        """;
        sb.append(sql);

        if(Objects.nonNull(postSearchParam.isPostIsPublic())){
            sb.append(" and a.post_is_public = ? ");
        }

        if(Objects.nonNull(postSearchParam.getCategoryId())){
            sb.append(" and b.category_id = ? ");
        }

        try(PreparedStatement psmt = connection.prepareStatement(sb.toString())){

            int index=1;
            psmt.setLong(index++, postSearchParam.getBlogId());

            if(Objects.nonNull(postSearchParam.isPostIsPublic())){
                psmt.setBoolean(index++, postSearchParam.isPostIsPublic());
            }

            if(Objects.nonNull(postSearchParam.getCategoryId())){
                psmt.setLong(index++, postSearchParam.getCategoryId());
            }

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    return rs.getLong(1);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return 0L;
    }
}
