package com.nhnacademy.blog.role.repository;

import com.nhnacademy.blog.role.doamin.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * TODO#3 - RoleRepository 구현
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

}
