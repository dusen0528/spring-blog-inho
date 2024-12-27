package com.nhnacademy.blog.blogmember.repository.impl;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;


import java.sql.*;
import java.util.Objects;
import java.util.Optional;
@Slf4j
@Repository
public class JdbcBlogMemberMappingRepository implements BlogMemberMappingRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBlogMemberMappingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(BlogMemberMapping blogMemberMapping) {
        String sql = """
                    insert into blog_member_mappings set
                        mb_no = ?,
                        blog_id = ?,
                        role_id = ?
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            int index=1;
            psmt.setLong(index++, blogMemberMapping.getMbNo());
            psmt.setLong(index++, blogMemberMapping.getBlogId());
            psmt.setString(index++,blogMemberMapping.getRoleId());
            return psmt;
        },keyHolder);

        if(rows>0){
            log.debug("blogMemberId:{}", keyHolder.getKey().longValue());
            ReflectionUtils.setField(blogMemberMapping, "blogMemberId", Objects.requireNonNull(keyHolder.getKey()).longValue());
        }

    }

    @Override
    public void deleteByBlogMemberMappingId(Long blogMemberId) {
        String sql = """
                    delete from blog_member_mappings where blog_member_id=?
                """;
        jdbcTemplate.update(sql, blogMemberId);
    }

    @Override
    public Optional<BlogMemberMapping> findByBlogMemberId(Long blogMemberId) {
        String sql = """
                select
                    blog_member_id,
                    mb_no,
                    blog_id,
                    role_id
                from blog_member_mappings
                where
                    blog_member_id=?
                """;

        try{
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql,new BlogMemberMappingRowMapper(),blogMemberId));
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<BlogMemberMapping> findByMbNoAndBlogId(Long mbNo, Long blogId) {
        String sql = """
                select
                    blog_member_id,
                    mb_no,
                    blog_id,
                    role_id
                from
                    blog_member_mappings
                where
                    mb_no=? and blog_id=?
                """;
        try{
            return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                    sql,
                    new BlogMemberMappingRowMapper(),
                    mbNo,
                    blogId
                )
            );
        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }

    }

    private static class BlogMemberMappingRowMapper implements RowMapper<BlogMemberMapping> {
        @Override
        public BlogMemberMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
            long dbBlogMemberId = rs.getLong("blog_member_id");
            long dbMbNo = rs.getLong("mb_no");
            long dbBlogId = rs.getLong("blog_id");
            String dbRoleId = rs.getString("role_id");

            return BlogMemberMapping.ofExistingBlogMemberMapping(
                    dbBlogMemberId,
                    dbMbNo,
                    dbBlogId,
                    dbRoleId
            );
        }
    }
}
