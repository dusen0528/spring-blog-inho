package com.nhnacademy.blog.role.doamin;

import com.nhnacademy.blog.common.config.ApplicationConfig;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 *  TODO#4-Test Role Entity Test 구현
 */
@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class RoleTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Role-entity-저장")
    void saveTest(){
        Role role = new Role(
                "ROLE_OWNER",
                "블로그-소유자",
                "블로그 소유자는 1명이상 존재할 수 없습니다."
        );
        entityManager.persist(role);

        Role newRole = entityManager.find(Role.class,role.getRoleId());

        Assertions.assertNotNull(role);
        Assertions.assertAll(
                ()->Assertions.assertEquals(role.getRoleId(), newRole.getRoleId()),
                ()->Assertions.assertEquals("ROLE_OWNER", newRole.getRoleId()),
                ()->Assertions.assertEquals("블로그-소유자", newRole.getRoleName()),
                ()->Assertions.assertEquals("블로그 소유자는 1명이상 존재할 수 없습니다.", newRole.getRoleDescription())
        );
    }

    @Test
    @DisplayName("Role-entity-수정")
    void updateTest(){
        Role role = new Role(
                "ROLE_OWNER",
                "블로그-소유자",
                "블로그 소유자는 1명이상 존재할 수 없습니다."
        );
        entityManager.persist(role);

        role.update("Blog-OWNER","A blog can have only one Owner");
        entityManager.flush();

        Role newRole = entityManager.find(Role.class,role.getRoleId());

        Assertions.assertNotNull(role);
        Assertions.assertAll(
                ()->Assertions.assertEquals(role.getRoleId(), newRole.getRoleId()),
                ()->Assertions.assertEquals("ROLE_OWNER", newRole.getRoleId()),
                ()->Assertions.assertEquals("Blog-OWNER", newRole.getRoleName()),
                ()->Assertions.assertEquals("A blog can have only one Owner", newRole.getRoleDescription())
        );

    }

    @Test
    @DisplayName("Role-entity-삭제")
    void deleteTest(){
        Role role = new Role(
                "ROLE_OWNER",
                "블로그-소유자",
                "블로그 소유자는 1명이상 존재할 수 없습니다."
        );
        entityManager.persist(role);
        entityManager.remove(role);
        entityManager.flush();

        Role newRole = entityManager.find(Role.class,role.getRoleId());

        Assertions.assertNull(newRole);
    }
}