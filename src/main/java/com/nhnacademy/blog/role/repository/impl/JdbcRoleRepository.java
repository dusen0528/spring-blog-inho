package com.nhnacademy.blog.role.repository.impl;

import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.dto.RoleUpdateRequestDto;
import com.nhnacademy.blog.role.repository.RoleRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Optional;

@Repository
public class JdbcRoleRepository implements RoleRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcRoleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(Role role) {

        String sql = """
                    insert into roles (
                        role_id,
                        role_name,
                        role_description
                    )
                    values(?,?,?);
                """;

        jdbcTemplate.update(connection->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index=1;
            psmt.setString(index++, role.getRoleId());
            psmt.setString(index++, role.getRoleName());
            psmt.setString(index++, role.getRoleDescription());
            return psmt;
        });
    }

    @Override
    public void update(RoleUpdateRequestDto roleUpdateRequestDto) {

        String sql = """
                    update roles set 
                        role_name=?,
                        role_description=? 
                     where 
                         role_id=?;
                """;
        jdbcTemplate.update(connection ->{
            PreparedStatement psmt = connection.prepareStatement(sql);
            int index=1;
            psmt.setString(index++, roleUpdateRequestDto.getRoleName());
            psmt.setString(index++, roleUpdateRequestDto.getRoleDescription());
            psmt.setString(index++, roleUpdateRequestDto.getRoleId());
            return psmt;
        });
    }

    @Override
    public void deleteByRoleId(String roleId) {

        String sql = """
                    delete from roles where role_id=?
                """;
        jdbcTemplate.update(sql,roleId);
    }

    @Override
    public Optional<Role> findByRoleId(String roleId) {

        String sql = """
                select role_id,role_name,role_description from roles where role_id=?
            """;

        return jdbcTemplate.queryForObject(sql, new RoleRowMapper(), roleId);
    }

    @Override
    public boolean existsByRoleId(String roleId) {

        String sql = """
                    select 1 from roles where role_id=?
                """;

        return Boolean.TRUE.equals(jdbcTemplate.query(sql, ResultSet::next, roleId));
    }

    private static class RoleRowMapper implements RowMapper<Optional<Role>> {
        @Override
        public Optional<Role> mapRow(ResultSet rs, int rowNum) throws SQLException {
            String dbRoleId = rs.getString("role_id");
            String dbRoleName = rs.getString("role_name");
            String dbRoleDescription = rs.getString("role_description");
            return Optional.of(new Role(dbRoleId, dbRoleName, dbRoleDescription));
        }
    }
}