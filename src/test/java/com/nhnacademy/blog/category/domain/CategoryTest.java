package com.nhnacademy.blog.category.domain;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.common.config.ApplicationConfig;
import com.nhnacademy.blog.topic.domain.Topic;
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
 * TODO#4 - Category entity 검증
 */

@ActiveProfiles("test")
@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = ApplicationConfig.class)
@Transactional
class CategoryTest {

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("category 저장")
    void saveTest(){

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);

        Topic topic = Topic.ofNewRootTopic("Internet",
                1
        );
        entityManager.persist(topic);

        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data-jpa",
                1
        );
        entityManager.persist(category);

        Category newCategory = entityManager.find(Category.class, category.getCategoryId());

        Assertions.assertNotNull(newCategory);
        Assertions.assertAll(
                ()->{
                    Assertions.assertEquals("spring-data-jpa", newCategory.getCategoryName());
                },
                ()->{
                    Assertions.assertEquals(1,newCategory.getCategorySec());
                },
                ()->{
                    Assertions.assertEquals( blog.getBlogId(), newCategory.getBlog().getBlogId());
                },
                ()->{
                    Assertions.assertEquals( topic.getTopicId(), newCategory.getTopic().getTopicId());
                },
                ()->{
                    Assertions.assertNotNull(newCategory.getCreatedAt());
                },
                ()->{
                    Assertions.assertNull(newCategory.getUpdatedAt());
                }
        );
    }

    @Test
    @DisplayName("category 수정")
    void updateTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",
                1
        );

        entityManager.persist(topic);

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);

        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data-jpa",
                1
        );

        entityManager.persist(category);

        category.update(category.getParentCategory(), category.getTopic(),"spring-data-elasticsearch",10);

        entityManager.flush();

        Category newCategory = entityManager.find(Category.class, category.getCategoryId());

        Assertions.assertNotNull(newCategory);
        Assertions.assertAll(
            ()->{
                Assertions.assertEquals("spring-data-elasticsearch", newCategory.getCategoryName());
            },
            ()->{
                Assertions.assertEquals(10,newCategory.getCategorySec());
            },
            ()->{
                Assertions.assertEquals( blog.getBlogId(), newCategory.getBlog().getBlogId());
            },
            ()->{
                Assertions.assertEquals( topic.getTopicId(), newCategory.getTopic().getTopicId());
            },
            ()->{
                Assertions.assertNotNull(newCategory.getCreatedAt());
            },
            ()->{
                Assertions.assertNotNull(newCategory.getUpdatedAt());
            }
        );
    }

    @Test
    @DisplayName("category 삭제")
    void deleteTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",
                1
        );

        entityManager.persist(topic);

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);

        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data-jpa",
                1
        );

        entityManager.persist(category);
        entityManager.remove(category);
        entityManager.flush();
        Category newCategory = entityManager.find(Category.class, category.getCategoryId());
        Assertions.assertNull(newCategory);
    }

    @Test
    @DisplayName("자식 카테고리 등록")
    void addChildCategoryTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",
                1
        );

        entityManager.persist(topic);

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);

        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data",
                1
        );

        category.addChildCategory(Category.ofNewSubCategory(category,blog,topic,"spring-data-jpa",1));
        category.addChildCategory(Category.ofNewSubCategory(category,blog,topic,"spring-data-redis",2));
        category.addChildCategory(Category.ofNewSubCategory(category,blog,topic,"spring-data-elasticsearch",3));
        entityManager.persist(category);

        entityManager.flush();
        entityManager.clear();

        Category newCategory = entityManager.find(Category.class, category.getCategoryId());
        Assertions.assertEquals(3, category.getChildrenCategories().size());

    }

    @Test
    @DisplayName("자식 카테고리 삭제")
    void removeChildCategoryTest(){
        Topic topic = Topic.ofNewRootTopic("Internet",
                1
        );

        entityManager.persist(topic);

        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "NHN아카데미-blog",
                "nhn-academy-marco",
                "NHN아카데미-블로그 입니다."
        );
        entityManager.persist(blog);

        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data",
                1
        );

        category.addChildCategory(Category.ofNewSubCategory(category,blog,topic,"spring-data-jpa",1));
        category.addChildCategory(Category.ofNewSubCategory(category,blog,topic,"spring-data-redis",2));
        category.addChildCategory(Category.ofNewSubCategory(category,blog,topic,"spring-data-elasticsearch",3));
        entityManager.persist(category);

        Category removeTargetCategory = category.getChildrenCategories().get(0);
        category.removeChildCategory(removeTargetCategory);

        entityManager.flush();
        entityManager.clear();

        Category newCategory = entityManager.find(Category.class, category.getCategoryId());
        Assertions.assertEquals(2, category.getChildrenCategories().size());

        Category removedCategory = entityManager.find(Category.class, removeTargetCategory.getCategoryId());
        Assertions.assertNull(removedCategory);
    }
}