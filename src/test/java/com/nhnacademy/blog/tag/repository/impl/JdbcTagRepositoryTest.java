package com.nhnacademy.blog.tag.repository.impl;

import com.nhnacademy.blog.common.context.Context;
import com.nhnacademy.blog.common.context.ContextHolder;
import com.nhnacademy.blog.common.transactional.DbConnectionThreadLocal;
import com.nhnacademy.blog.tag.domain.Tag;
import com.nhnacademy.blog.tag.dto.TagUpdateRequest;
import com.nhnacademy.blog.tag.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import java.util.Optional;

@Slf4j
class JdbcTagRepositoryTest {

    static TagRepository tagRepository;

    @BeforeAll
    static void beforeAll() {
        Context context = ContextHolder.getApplicationContext();
        tagRepository = (JdbcTagRepository) context.getBean(JdbcTagRepository.BEAN_NAME);
    }

    @BeforeEach
    void setUp() {
        tagRepository = new JdbcTagRepository();
        DbConnectionThreadLocal.initialize();
    }

    @AfterEach
    void tearDown() {
        DbConnectionThreadLocal.setSqlError(true);
        DbConnectionThreadLocal.reset();
    }

    @Test
    @DisplayName("테그 생성")
    void save() {
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        Optional<Tag> tagOptional = tagRepository.findById(tag.getTagId());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(tag.getTagId()),
                ()->Assertions.assertEquals(tag.getTagId(), tagOptional.get().getTagId()),
                ()->Assertions.assertEquals(tag.getTagName(), tagOptional.get().getTagName())
        );

    }

    @Test
    @DisplayName("태그 수정")
    void update() {
        //given
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);

        TagUpdateRequest tagUpdateRequest = new TagUpdateRequest(tag.getTagId(),"java-17");
        tagRepository.update(tagUpdateRequest);
        //when
        Optional<Tag> tagOptional = tagRepository.findById(tag.getTagId());
        log.debug("tag: {}", tag);

        //then
        Assertions.assertAll(
                ()->Assertions.assertEquals(tag.getTagId(), tagOptional.get().getTagId()),
                ()->Assertions.assertEquals("java-17", tagOptional.get().getTagName())
        );

    }

    @Test
    @DisplayName("태그 조회")
    void findById() {

        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        Optional<Tag> tagOptional = tagRepository.findById(tag.getTagId());
        Assertions.assertAll(
                ()->Assertions.assertNotNull(tag.getTagId()),
                ()->Assertions.assertEquals(tag.getTagId(), tagOptional.get().getTagId()),
                ()->Assertions.assertEquals(tag.getTagName(), tagOptional.get().getTagName())
        );
    }

    @Test
    @DisplayName("태그조회_by_tagName")
    void findByTagName() {
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        Optional<Tag> tagOptional = tagRepository.findByTagName("java-21");
        Assertions.assertAll(
                ()->Assertions.assertNotNull(tag.getTagId()),
                ()->Assertions.assertEquals(tag.getTagId(), tagOptional.get().getTagId()),
                ()->Assertions.assertEquals(tag.getTagName(), tagOptional.get().getTagName())
        );
    }

    @Test
    @DisplayName("태그삭제_by_tagId")
    void deleteByTagId() {
        //given
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        //when
        tagRepository.deleteByTagId(tag.getTagId());
        boolean actual = tagRepository.existsByTagId(tag.getTagId());

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("태그삭제_by_tagName")
    void deleteByTagName() {
        //given
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        //when
        tagRepository.deleteByTagName(tag.getTagName());
        boolean actual = tagRepository.existsByTagId(tag.getTagId());

        //then
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("태그_존재여부_by_tagId")
    void existsByTagId() {
        //given
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        //when
        boolean actual = tagRepository.existsByTagId(tag.getTagId());

        //then
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("태그_존재여부_by_tagName")
    void existsByTagName() {
        //given
        Tag tag = Tag.ofNewTag("java-21");
        tagRepository.save(tag);
        log.debug("tag: {}", tag);

        //when
        boolean actual = tagRepository.existsByTagName(tag.getTagName());

        //then
        Assertions.assertTrue(actual);
    }

}