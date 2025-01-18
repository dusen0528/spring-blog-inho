package com.nhnacademy.blog.common.interceptor;

import com.nhnacademy.blog.member.auth.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

/**
 * Spring에서 Interceptor는 특정 요청이 컨트롤러에 도달하기 전,
 * 또는 응답이 클라이언트로 전달되기 전에 추가적인 로직을 실행할 수 있도록 하는 컴포넌트입니다.
 * Spring MVC에서 제공하는 인터셉터는 주로 **전처리(pre-processing)**와 후처리(post-processing) 작업에 사용됩니다.
 * org.springframework.web.servlet.HandlerInterceptor interface를 구현 합니다.
 */
@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * preHandle구현
         * 요청(Request)이 Controller에 도달하기 전에 실행됩니다
         * @see com.nhnacademy.blog.common.interceptor.LoginCheckInterceptor 를 참고하여 구현 합니다.
         *
         */

        HttpSession session = request.getSession(false);
        if(Objects.nonNull(session)) {
            LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
            if(Objects.isNull(loginMember)) {
                log.debug("LoginCheckInterceptor:preHandle");
                response.sendRedirect("/index.do");
            }
        }else {
            log.debug("LoginCheckInterceptor:preHandle");
            response.sendRedirect("/index.do");
        }

        //true를 반환 합니다. -> 다른 Interceptor가 존재하면 실행 합니다.
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //Controller가 요청을 처리한 후, View로 데이터를 전달하기 전에 실행됩니다.
        //로그를 작성해서 동작하는지 확인합니다.
        log.debug("LoginCheckInterceptor:postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //요청 처리의 모든 작업(뷰 렌더링 포함)이 완료된 후에 실행됩니다.
        //로그를 작성해서 동작하는지 확인합니다.
        log.debug("LoginCheckInterceptor:afterCompletion");
    }
}
