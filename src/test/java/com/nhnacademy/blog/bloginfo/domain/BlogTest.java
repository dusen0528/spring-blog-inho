package com.nhnacademy.blog.bloginfo.domain;

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
 * TODO#2-TEST blog entity Test 구현
 */

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class BlogTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("blog 등록")
    void saveTest(){
        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );

        entityManager.persist(blog);
        Blog newBlog = entityManager.find(Blog.class, blog.getBlogId());

        Assertions.assertNotNull(newBlog);
        Assertions.assertAll(
                ()->Assertions.assertTrue(newBlog.isBlogMain()),
                ()->Assertions.assertEquals("marco", newBlog.getBlogFid()),
                ()->Assertions.assertTrue(newBlog.isBlogMain()),
                ()->Assertions.assertEquals("NHN아카데미-blog", newBlog.getBlogName()),
                ()->Assertions.assertEquals("NHN아카데미-블로그 입니다.", newBlog.getBlogDescription()),
                ()->Assertions.assertNotNull(newBlog.getCreatedAt()),
                ()->Assertions.assertNull(newBlog.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("blog 수정")
    void updateTest(){
        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );

        entityManager.persist(blog);

        blog.update(
                "test blog",
                "nhn-academy-test",
                "test's blog 입니다.",
                false
        );
        entityManager.flush();

        Blog newBlog = entityManager.find(Blog.class, blog.getBlogId());

        Assertions.assertNotNull(newBlog);
        Assertions.assertAll(
                ()->Assertions.assertTrue(newBlog.isBlogMain()),
                ()->Assertions.assertEquals("test blog", newBlog.getBlogName()),
                ()->Assertions.assertEquals("nhn-academy-test", newBlog.getBlogMbNickname()),
                ()->Assertions.assertEquals("test's blog 입니다.", newBlog.getBlogDescription()),
                ()->Assertions.assertFalse(newBlog.getBlogIsPublic()),
                ()->Assertions.assertNotNull(newBlog.getCreatedAt()),
                ()->Assertions.assertNotNull(newBlog.getUpdatedAt())
        );
    }

    @Test
    @DisplayName("blog 삭제")
    void deleteTest(){
        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);
        entityManager.remove(blog);
        entityManager.flush();
        Blog newBlog = entityManager.find(Blog.class, blog.getBlogId());
        Assertions.assertNull(newBlog);
    }

}