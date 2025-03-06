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
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO#3 local환경에서의 테스트 데이터 생성
 * Spring Application 시작시 블로그 회원가입 블로그 생성
 * - ApplicationReadyEvent 를 구독 합니다.
 * - erd를 기반으로 기본데이터를 생성합니다.
 * - 데이터는 h2에 저장됩니다.
 */

@Profile("local")
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
        //TODO#3-1 local에서 테스트를 위해서 사용할 이메일/비밀번호 설정
        String username = "inho@nhnacademy.com";
        String password = "1234";

        //TODO#3-2 해당 username이 존재하면 return; 호출 합니다.
        boolean isExist = memberRepository.existsByMbEmail(username);
        if(isExist) {
            return;
        }

        //TODO#3-3 topic 등록
        Topic topic = Topic.ofNewRootTopic("spring",1);
        topicRepository.save(topic);

        //TODO#3-4 ROLE 등록
        Role role = new Role("ROLE_OWNER", "블로그_소유자", "블로그의 소유자 , 운영자");
        roleRepository.save(role);

        //TODO#3-5 member 등록
        Member member = Member.ofNewMember(
                username,
                "인호",
                passwordEncoder.encode(password),
                "01011112222"
        );

        memberRepository.save(member);

        //TODO#3-6 blog 등록
        Blog blog = Blog.ofNewBlog(
                "inho",
                true,
                "inho's blog",
                "inho",
                "Spring Blog!"
        );

        //TODO#3-7 member,blog, role 연결
        BlogMemberMapping blogMemberMapping = BlogMemberMapping.ofNewBlogMemberMapping(
                member,
                blog,
                role
        );
        blog.addBlogMemberMapping(blogMemberMapping);
        blogRepository.save(blog);

        //TODO#3-8 블로그 카테고리 저장
        Category category = Category.ofNewRootCategory(
                blog,
                topic,
                "spring-data-jpa",
                1
        );
        categoryRepository.save(category);
    }
}
