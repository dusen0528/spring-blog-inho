package com.nhnacademy.blog.common.advice;

import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.web.auth.LoginController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackageClasses = {LoginController.class})
public class LoginAdvice {

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
