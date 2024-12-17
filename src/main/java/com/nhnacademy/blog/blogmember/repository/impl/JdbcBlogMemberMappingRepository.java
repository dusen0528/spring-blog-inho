package com.nhnacademy.blog.blogmember.repository.impl;

import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMemberMappingRepository;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;

import java.sql.*;
import java.util.Optional;

@Repository(name = JdbcBlogMemberMappingRepository.BEAN_NAME)
public class JdbcBlogMemberMappingRepository implements BlogMemberMappingRepository {
    public static final String BEAN_NAME = "jdbcBlogMemberMappingRepository";

    @Override
    public void save(BlogMemberMapping blogMemberMapping) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    insert into blog_members_mapping set 
                        mb_no = ?,
                        blog_id = ?,
                        role_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            int index=1;
            psmt.setLong(index++, blogMemberMapping.getMbNo());
            psmt.setLong(index++, blogMemberMapping.getBlogId());
            psmt.setString(index++,blogMemberMapping.getRoleId());
            psmt.executeUpdate();

            try(ResultSet generatedKeys = psmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long blogMemberId = generatedKeys.getLong(1);
                    ReflectionUtils.setField(blogMemberMapping, "blogMemberId", blogMemberId);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByBlogMemberMappingId(Long blogMemberId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from blog_members_mapping where blog_member_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,blogMemberId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<BlogMemberMapping> findByBlogMemberId(Long blogMemberId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select 
                    blog_member_id, 
                    mb_no, 
                    blog_id, 
                    role_id 
                from blog_members_mapping
                where 
                    blog_member_id=?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setLong(index++,blogMemberId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    long dbBlogMemberId = rs.getLong("blog_member_id");
                    long dbMbNo = rs.getLong("mb_no");
                    long dbBlogId = rs.getLong("blog_id");
                    String dbRoleId = rs.getString("role_id");
                    BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(dbBlogMemberId,dbMbNo,dbBlogId,dbRoleId);
                    return Optional.of(blogMemberMapping);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<BlogMemberMapping> findByMbNoAndBlogId(Long mbNo, Long blogId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select 
                    blog_member_id, 
                    mb_no, 
                    blog_id, 
                    role_id
                from 
                    blog_members_mapping
                where 
                    mb_no=? and blog_id=?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setLong(index++,mbNo);
            psmt.setLong(index++,blogId);

            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    long dbBlogMemberId = rs.getLong("blog_member_id");
                    long dbMbNo = rs.getLong("mb_no");
                    long dbBlogId = rs.getLong("blog_id");
                    String dbRoleId = rs.getString("role_id");
                    BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofExistingBlogMemberMapping(dbBlogMemberId,dbMbNo,dbBlogId,dbRoleId);
                    return Optional.of(blogMemberMapping);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }
}
