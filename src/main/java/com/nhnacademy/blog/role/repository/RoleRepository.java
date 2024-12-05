package com.nhnacademy.blog.role.repository;

import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.dto.RoleUpdateRequestDto;

import java.util.Optional;

public interface RoleRepository {
    //role등록
    int save(Role role);
    //role수정
    int update(RoleUpdateRequestDto roleUpdateRequestDto);
    //role삭제
    int delete(String roleId);
    //role조회
    Optional<Role> findByRoleId(String roleId);
    //role 존재여부 체크
    boolean existsByRoleId(String roleId);
}
