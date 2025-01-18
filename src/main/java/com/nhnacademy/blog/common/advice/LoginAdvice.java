package com.nhnacademy.blog.common.advice;

import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.web.auth.LoginController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
/**
 * TODO#5 - basePackageClasses 속성을 지정하여 LoginAdvice를 지정할 대상 class를 지정 합니다.
 * - 대상 클래스 : LoginController.class 입니다.
 * - @ControllerAdvice(basePackageClasses) 선언 합니다.
 */
public class LoginAdvice {

    /**
     * TODO#5-1 unauthorizedHandler method는 로그인 실폐시 , 즉  UnauthorizedException exception이 발생하면
     * unauthorizedHandler 메서드가 exception을 잡아서 처리 합니다.
     */
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public String unauthorizedHandler(UnauthorizedException e, HttpServletRequest httpServletRequest, Model model) {
        log.error(e.getMessage(), e);
        String referer = httpServletRequest.getHeader("Referer");

        model.addAttribute("statusCode", HttpStatus.UNAUTHORIZED.value());
        model.addAttribute("referer", referer);
        model.addAttribute("errorMessage", e.getMessage());
        return "error/common-error";
    }
}
