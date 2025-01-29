package com.nhnacademy.blog.common.security.handler;

import com.nhnacademy.blog.bloginfo.repository.BlogRepository;
import com.nhnacademy.blog.common.security.userdetail.MemberDetails;
import com.nhnacademy.blog.member.event.MemberLoginEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final BlogRepository blogRepository;

    //이벤트 발행을 위한 ApplicationEventPublisher
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //로그인한 사용자의 이메일을 가져옵니다 (username으로 이메일을 사용하는 경우)
        String email = authentication.getName();

        //MemberLoginEvent 생성: 로그인 이벤트를 발생시키고 이메일을 포함시킵니다.
        MemberLoginEvent loginEvent = new MemberLoginEvent(this,email);

        //ApplicationEventPublisher 객체의 publishEvent()를 호출하여 이벤트를 발생시킴니다.
        eventPublisher.publishEvent(loginEvent);

        //사용자의 메인 블로그 아이디 구하기
        MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();

        long mbNo = memberDetails.getMemberResponse().getMbNo();
        String blogFid = blogRepository.blogFidFromMainBlog(mbNo);
        String redirectUrl;
        if(StringUtils.isEmpty(blogFid)) {
            redirectUrl = "/";
        }else{
            redirectUrl = "/blog/%s/index.do".formatted(blogFid);
        }

        //로그인 성공 후 /index.do 페이지로 리다이렉션
        response.sendRedirect(redirectUrl);
    }
}
