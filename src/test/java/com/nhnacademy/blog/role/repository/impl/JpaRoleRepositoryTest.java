package com.nhnacademy.blog.role.repository.impl;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.repository.JpaRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class})
@Transactional
class JpaRoleRepositoryTest {

    @Autowired
    JpaRoleRepository roleRepository;

    @Test
    @DisplayName("권한등록")
    void save() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.saveAndFlush(role);
        Assertions.assertNotNull(role.getRoleId());
    }

    @Test
    @DisplayName("권한수정")
    void update() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.saveAndFlush(role);
        String roleId = role.getRoleId();

        Optional<Role> optionalRole = roleRepository.findById(roleId);

        optionalRole.ifPresent(dbRole -> {
            dbRole.update("roleName-수정","roleDescription-수정");
            roleRepository.saveAndFlush(dbRole);
        });

        Optional<Role> dbRoleOptional = roleRepository.findById(roleId);

        Assertions.assertTrue(dbRoleOptional.isPresent());

        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals(roleId, dbRoleOptional.get().getRoleId());
                },
                ()->{
                    Assertions.assertEquals("roleName-수정",dbRoleOptional.get().getRoleName());
                },
                ()->{
                    Assertions.assertEquals("roleDescription-수정",dbRoleOptional.get().getRoleDescription());
                }
        );
    }

    @Test
    @DisplayName("권한삭제")
    void delete() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.saveAndFlush(role);
        String roleId = role.getRoleId();

        Optional<Role> dbRoleOptional = roleRepository.findById(roleId);
        dbRoleOptional.ifPresent(dbRole -> {
            roleRepository.deleteById(roleId);
            roleRepository.flush();
        });

        boolean actual = roleRepository.existsById(roleId);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("권한조회")
    void findByRoleId() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.saveAndFlush(role);
        String roleId = role.getRoleId();

        Optional<Role> dbRoleOptional = roleRepository.findById(roleId);

        Assertions.assertTrue(dbRoleOptional.isPresent());
        Assertions.assertEquals(role,dbRoleOptional.get());
    }

    @Test
    @DisplayName("권한존재여부 : true")
    void existsByRoleId() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        String roleId = role.getRoleId();
        roleRepository.saveAndFlush(role);

        boolean actual = roleRepository.existsById(roleId);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("권한존재여부 : false")
    void notExistsByRoleId() {
        boolean actual = roleRepository.existsById("ROLE_TESTADMIN");
        Assertions.assertFalse(actual);
    }
}
