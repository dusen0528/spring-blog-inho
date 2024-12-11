package com.nhnacademy.blog.blogmember.repository.impl;

import com.nhnacademy.blog.blogmember.domain.BlogMembersMapping;
import com.nhnacademy.blog.blogmember.repository.BlogMembersMappingRepository;
import com.nhnacademy.blog.common.annotation.stereotype.Repository;
import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.reflection.ReflectionUtils;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;

import java.sql.*;
import java.util.Optional;

@Repository(name = JdbcBlogMembersMappingRepository.BEAN_NAME)
public class JdbcBlogMembersMappingRepository implements BlogMembersMappingRepository {
    public static final String BEAN_NAME = "jdbcBlogMembersMappingRepository";

    @Override
    public void save(BlogMembersMapping blogMembersMapping) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    insert into blog_members_mapping set 
                        mb_no = ?,
                        blog_id = ?,
                        role_id = ?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            int index=1;
            psmt.setLong(index++, blogMembersMapping.getMbNo());
            psmt.setLong(index++, blogMembersMapping.getBlogId());
            psmt.setString(index++,blogMembersMapping.getRoleId());
            psmt.executeUpdate();

            try(ResultSet generatedKeys = psmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Long blogMembersId = generatedKeys.getLong(1);
                    ReflectionUtils.setField(blogMembersMapping, "blogMembersId", blogMembersId);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public void deleteByBlogMemberMappingId(Long blogMembersId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    delete from blog_members_mapping where blog_members_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            psmt.setLong(1,blogMembersId);
            psmt.executeUpdate();
        }catch (SQLException e){
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<BlogMembersMapping> findByBlogMembersId(Long blogMembersId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                select 
                    blog_members_id, 
                    mb_no, 
                    blog_id, 
                    role_id 
                from blog_members_mapping
                where 
                    blog_members_id=?
                """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setLong(index++,blogMembersId);
            try(ResultSet rs = psmt.executeQuery()){
                if(rs.next()){
                    long dbBlogMembersId = rs.getLong("blog_members_id");
                    long dbMbNo = rs.getLong("mb_no");
                    long dbBlogId = rs.getLong("blog_id");
                    String dbRoleId = rs.getString("role_id");
                    BlogMembersMapping blogMembersMapping = BlogMembersMapping.ofExistingBlogMemberMapping(dbBlogMembersId,dbMbNo,dbBlogId,dbRoleId);
                    return Optional.of(blogMembersMapping);
                }
            }
        }catch (SQLException e){
            throw new DatabaseException(e);
        }

        return Optional.empty();
    }
}
