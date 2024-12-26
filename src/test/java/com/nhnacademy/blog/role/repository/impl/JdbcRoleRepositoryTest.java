package com.nhnacademy.blog.role.repository.impl;

import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.dto.RoleUpdateRequestDto;
import com.nhnacademy.blog.role.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.context.ApplicationContext;

/**
 *  TODO#3-2 JdbcRoleRepositoryTest Spring 기반의 Repository Test환경 구성
 *  - TODO#3-1 참고해서 구현 합니다.
 */
@Slf4j
class JdbcRoleRepositoryTest {
    static RoleRepository roleRepository;

    @BeforeAll
    static void beforeAll() {
        ApplicationContext context = ContextHolder.getApplicationContext();
        roleRepository = (RoleRepository) context.getBean(JdbcRoleRepository.BEAN_NAME);
    }

    @BeforeEach
    void setUp() {
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("권한등록")
    void save() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        Assertions.assertNotNull(role.getRoleId());
    }

    @Test
    @DisplayName("권한수정")
    void update() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        RoleUpdateRequestDto roleUpdateRequestDto = new RoleUpdateRequestDto(role.getRoleId(),"roleName-수정","roleDescription-수정");
        roleRepository.update(roleUpdateRequestDto);

        Role dbRole = roleRepository.findByRoleId(role.getRoleId()).get();

        Assertions.assertAll(
            ()->{
                Assertions.assertEquals(roleUpdateRequestDto.getRoleId(),dbRole.getRoleId());
            },
            ()->{
                Assertions.assertEquals(roleUpdateRequestDto.getRoleName(),dbRole.getRoleName());
            },
            ()->{
                Assertions.assertEquals(roleUpdateRequestDto.getRoleDescription(),dbRole.getRoleDescription());
            }
        );
    }

    @Test
    @DisplayName("권한삭제")
    void delete() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        Role dbRole = roleRepository.findByRoleId(role.getRoleId()).get();
        log.debug("dbRole: {}", dbRole);

        roleRepository.deleteByRoleId(role.getRoleId());

        boolean actual = roleRepository.existsByRoleId(role.getRoleId());
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("권한조회")
    void findByRoleId() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        Role dbRole = roleRepository.findByRoleId(role.getRoleId()).get();
        log.debug("dbRole: {}", dbRole);

        Assertions.assertEquals(role,dbRole);
    }

    @Test
    @DisplayName("권한존재여부 : true")
    void existsByRoleId() {
        Role role = new Role("ROLE_SYSADMIN","전체-시스템-관리자","전체-블로그-시스템-관리자");
        roleRepository.save(role);

        boolean actual = roleRepository.existsByRoleId(role.getRoleId());
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("권한존재여부 : false")
    void notExistsByRoleId() {
        boolean actual = roleRepository.existsByRoleId("ROLE_TESTADMIN");
        Assertions.assertFalse(actual);
    }
}