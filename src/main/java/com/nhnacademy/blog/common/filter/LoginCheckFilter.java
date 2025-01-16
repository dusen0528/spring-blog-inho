package com.nhnacademy.blog.common.filter;

import com.nhnacademy.blog.member.auth.LoginMember;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class LoginCheckFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        HttpSession session = httpServletRequest.getSession(false);
        if(Objects.nonNull(session)) {
            LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
            if(Objects.isNull(loginMember)) {
                log.debug("LogincheckFilter:execute");
                httpServletResponse.sendRedirect("/index.do");
            }
            chain.doFilter(request, response);
        }else {
            log.debug("LogincheckFilter:execute");
            httpServletResponse.sendRedirect("/index.do");
        }
    }

}