package com.nhnacademy.blog.blogmember.domain;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.role.doamin.Role;
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

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class BlogMemberMappingTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("BlogMemberMapping 등록")
    void saveTest(){

        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );
        entityManager.persist(member);
        long memberNo = member.getMbNo();

        Role role = new Role(
                "ROLE_OWNER",
                "블로그-소유자",
                "블로그 소유자는 1명이상 존재할 수 없습니다."
        );
        entityManager.persist(role);
        String roleId = role.getRoleId();

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);
        long blogId = blog.getBlogId();

        BlogMemberMapping mapping = BlogMemberMapping
                .ofNewBlogMemberMapping(
                        member,
                        blog,
                        role
                );
        entityManager.persist(mapping);

        Assertions.assertNotNull(mapping);
        Assertions.assertAll(
                ()->Assertions.assertEquals(memberNo, mapping.getMember().getMbNo()),
                ()->Assertions.assertEquals(blogId, mapping.getBlog().getBlogId()),
                ()->Assertions.assertEquals(roleId, mapping.getRole().getRoleId())
        );
    }

    @Test
    @DisplayName("BlogMemberMapping 삭제")
    void deleteTest(){

        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                "12345",
                "01012345678"
        );
        entityManager.persist(member);
        long memberNo = member.getMbNo();

        Role role = new Role(
                "ROLE_OWNER",
                "블로그-소유자",
                "블로그 소유자는 1명이상 존재할 수 없습니다."
        );
        entityManager.persist(role);
        String roleId = role.getRoleId();

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);
        long blogId = blog.getBlogId();

        BlogMemberMapping mapping = BlogMemberMapping
                .ofNewBlogMemberMapping(
                        member,
                        blog,
                        role
                );
        entityManager.persist(mapping);

        entityManager.remove(mapping);
        entityManager.flush();

        BlogMemberMapping newMapping = entityManager.find(BlogMemberMapping.class, mapping.getMember().getMbNo());
        Assertions.assertNull(newMapping);
    }
}