package com.nhnacademy.blog.role.repository;

import com.nhnacademy.blog.role.doamin.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TODO#3 - TEST
 * - RoleRepository를 이용해서 변경하세요
 * - MemberRepository를 참고하세요
 */
class RoleRepositoryTest {

    @Test
    @DisplayName("Role-entity-저장")
    void saveTest(){

    }

    @Test
    @DisplayName("Role-entity-수정")
    void updateTest(){
        //update 구현시 roleRepository.save(Role) <-- 호출하면 entity가 존재하면 update 합니다. 존재하지 않다면 insert 합니다.
    }

    @Test
    @DisplayName("Role-entity-삭제")
    void deleteTest(){

    }

}