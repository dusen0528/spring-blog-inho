package com.nhnacademy.blog.common.listener;

import com.nhnacademy.blog.bloginfo.domain.Blog;
import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.blogmember.domain.BlogMemberMapping;
import com.nhnacademy.blog.category.domain.Category;
import com.nhnacademy.blog.category.repository.CategoryRepository;
import com.nhnacademy.blog.member.domain.Member;
import com.nhnacademy.blog.member.repository.MemberRepository;
import com.nhnacademy.blog.role.doamin.Role;
import com.nhnacademy.blog.role.repository.RoleRepository;
import com.nhnacademy.blog.topic.domain.Topic;
import com.nhnacademy.blog.topic.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO#3 - Spring Application 시작시 블로그 회원가입 블로그 생성
 * - ApplicationReadyEvent 를 구독 합니다.
 * - erd를 기반으로 기본데이터를 생성합니다.
 * - 데이터는 h2에 저장됩니다.
 */
@Component
@RequiredArgsConstructor
public class ApplicationStartListener implements ApplicationListener<ApplicationReadyEvent> {

    private final TopicRepository topicRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        //TODO#3-1.topic 등록
        Topic topic = Topic.ofNewRootTopic("spring",1);
        topicRepository.save(topic);

        //TODO#3-2.ROLE 등록
        Role role = new Role("ROLE_OWNER", "블로그_소유자", "블로그의 소유자 , 운영자");
        roleRepository.save(role);

        //TODO#3-3.member 등록
        Member member = Member.ofNewMember(
                "marco@nhnacademy.com",
                "마르코",
                passwordEncoder.encode("nhnacademy"),
                "01011112222"
        );
        memberRepository.save(member);

        //TODO#3-4 blog 등록
        Blog blog = Blog.ofNewBlog(
                "marco",
                true,
                "marco's blog",
                "marco",
                "Spring Blog!"
        );

        //TODO#3-5 member,blog, role 연결
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(
                member,
                blog,
                role
        );
        blog.addBlogMemberMapping(blogMemberMapping);
        blogRepository.save(blog);

        //TODO#3-6 블로그 카테고리 저장
        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data-jpa",
                1
        );
        categoryRepository.save(category);

    }
}
