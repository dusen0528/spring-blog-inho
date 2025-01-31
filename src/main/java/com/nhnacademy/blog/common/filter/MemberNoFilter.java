package com.nhnacademy.blog.common.filter;

import com.nhnacademy.blog.auth.MemberThreadLocal;
import com.nhnacademy.blog.common.security.userdetail.MemberDetails;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TODO#4  - MemberNoFilter 구현
 *  - 로그인되어 있다면 회원 번호를 어디서든지 접근해서 쉽게 사용할 수 있도록  MemberThreadLocal을 이용하여 mbNo 값을 할당 합니다.
 *  - Thread 작업이 종료되면 (wait 상태로 되돌아 간다면) memberNo를 초기화 합니다.
 */

@Slf4j
/**
 * TODO#4-1 모든 요청에 한 번만 수행될 수 있도록 OncePerRequestFilter를 확장(extend) 해서 구현 합니다.
 */
public class MemberNoFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //TODO#4-2 SecurityContextHolder로 부터 Authentication 객체를 획득 합니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //TODO#4-3 isAuthenticated() 호출해서 인증여부를 판단 합니다. 인증이 되었다면 MemberThreadLocal에 memberNo를 할당 합니다.
        if(isAuthenticated(authentication)) {

            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            long memberNo = memberDetails.getMemberResponse().getMbNo();
            MemberThreadLocal.setMemberNo(memberNo);
            log.debug("memberNo: {}", memberNo);
        }

        //TODO#4-4 다음 filter를 호출 합니다.
        filterChain.doFilter(request, response);

        //TODO#4-5 작업이 종료되기 전 MemberThreadLocal의 removeMemberNo()메서드르 호출해서 memberNo값을 초기화 합니다.
        MemberThreadLocal.removeMemberNo();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        /* TODO#4-6 Filter가 동작할 때 exclude url의 조건을 지정 할 수 있습니다.
            - /resources/** 하위의 모든 경로는 공개되어 있는 static resource 임으로 Filter가 동작되지 않도록 제외할 수 있습니다.
            - 이미 SecurityConf.java 에서 /resources/** 제외되어 있습니다.
            - 역할과 사용법 정도로 참고 합니다.
         */
        log.debug("shouldNotFilter:{}", request.getRequestURI());
        return request.getRequestURI().startsWith("/resources");
    }

    public boolean isAuthenticated(Authentication authentication) {
        /**
         * - TODO#4-7 Spring security는 인증되지 않는(로그인 안된) 사용자는 AnonymousAuthenticationToken을 기본으로 생성 합니다.
         * - 로그인 여부에 따른 인증여부를 체크하기 위해서  체크해야될 조건은 다음과 같습니다.
         *  + authentication.isAuthenticated()
         *  + !(authentication instanceof AnonymousAuthenticationToken)
         */
        return  authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken);
    }

}
