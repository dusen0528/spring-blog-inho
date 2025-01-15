package com.nhnacademy.blog.common.filter;

import com.nhnacademy.blog.member.auth.LoginMember;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Objects;

public class LogincheckFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        HttpSession session = httpServletRequest.getSession(false);
        if(Objects.nonNull(session)) {
            LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
            if(Objects.isNull(loginMember)) {
                httpServletResponse.sendRedirect("/index.do");
            }
            chain.doFilter(request, response);
        }

    }
}