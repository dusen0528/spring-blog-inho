package com.nhnacademy.blog.common.security.handler;

import com.nhnacademy.blog.member.event.MemberLoginEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * TODO#3 - CustomAuthenticationSuccessHandler는 Spring Security에서 로그인 성공 후 수행할 커스텀 로직을 처리하는 핸들러입니다.
 *  - 로그인 성공 시 사용자의 이메일을 기반으로 커스텀 이벤트를 발행하고, 지정된 페이지로 리다이렉션합니다.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    //TODO#3-1 이벤트 발행을 위한 ApplicationEventPublisher
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //TODO#3-2 로그인한 사용자의 이메일을 가져옵니다 (username으로 이메일을 사용하는 경우)
        String email = authentication.getName();

        //TODO#3-3 MemberLoginEvent 생성: 로그인 이벤트를 발생시키고 이메일을 포함시킵니다.
        MemberLoginEvent loginEvent = new MemberLoginEvent(this, email);

        //TODO#3-4 ApplicationEventPublisher 객체의 publishEvent()를 호출하여 이벤트를 발생시킴니다.
        eventPublisher.publishEvent(loginEvent);

        //TODO#3-5 로그인 성공 후 /index.do 페이지로 리다이렉션
        response.sendRedirect("/index.do");
    }
}
