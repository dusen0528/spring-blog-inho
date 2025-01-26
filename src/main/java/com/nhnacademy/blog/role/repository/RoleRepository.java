package com.nhnacademy.blog.role.repository;

import com.nhnacademy.blog.role.doamin.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

}
