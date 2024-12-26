package com.nhnacademy.blog.role.repository.impl;

import com.nhnacademy.blog.common.db.exception.DatabaseException;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.dto.RoleUpdateRequestDto;
import com.nhnacademy.blog.role.repository.RoleRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

/**
 * TODO#2-2 jdbc-api -> JdbcTemplate 기반으로 Repository 구현
 * @Repository <- org.springframework.stereotype.Repository 입니다.
 */
@Repository
public class JdbcRoleRepository implements RoleRepository {
    public static final String BEAN_NAME = "jdbcRoleRepository";

    @Override
    public void save(Role role) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    insert into roles (
                        role_id,
                        role_name,
                        role_description
                    )
                    values(?,?,?);
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setString(index++, role.getRoleId());
            psmt.setString(index++, role.getRoleName());
            psmt.setString(index++, role.getRoleDescription());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void update(RoleUpdateRequestDto roleUpdateRequestDto) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    update roles set 
                        role_name=?,
                        role_description=? 
                     where 
                         role_id=?;
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setString(index++, roleUpdateRequestDto.getRoleName());
            psmt.setString(index++, roleUpdateRequestDto.getRoleDescription());
            psmt.setString(index++, roleUpdateRequestDto.getRoleId());
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }

    }

    @Override
    public void deleteByRoleId(String roleId) {
        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                    delete from roles where role_id=?
                """;

        try ( PreparedStatement psmt = connection.prepareStatement(sql)){
            int index=1;
            psmt.setString(index++, roleId);
            psmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Optional<Role> findByRoleId(String roleId) {

        Connection connection = DbConnectionThreadLocal.getConnection();

        String sql = """
                select role_id,role_name,role_description from roles where role_id=?
            """;

        try(PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, roleId);
            try(ResultSet rs = psmt.executeQuery()) {
                while (rs.next()) {
                    String dbRoleId = rs.getString(1);
                    String dbRoleName = rs.getString(2);
                    String dbRoleDescription = rs.getString(3);
                    return Optional.of(new Role(dbRoleId, dbRoleName, dbRoleDescription));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByRoleId(String roleId) {
        Connection connection = DbConnectionThreadLocal.getConnection();
        String sql = """
                    select 1 from roles where role_id=?
                """;
        try(PreparedStatement psmt = connection.prepareStatement(sql)) {
            psmt.setString(1, roleId);
            try(ResultSet rs = psmt.executeQuery()) {
                if(rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
        return false;
    }
}