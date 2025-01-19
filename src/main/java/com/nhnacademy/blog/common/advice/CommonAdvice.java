package com.nhnacademy.blog.common.advice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice
public class CommonAdvice {

    @ResponseStatus(code= HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public String bindExceptionHandler(BindException e, HttpServletRequest httpServletRequest, Model model) {
        log.error(e.getMessage(), e);
        String referer = httpServletRequest.getHeader("Referer");

        model.addAttribute("referer", referer);
        StringBuilder sb  = new StringBuilder();

        //sb에 담길 error message를 작성하세요.
        e.getBindingResult().getAllErrors().forEach(error ->{
            if(error instanceof FieldError fieldError){
                log.error("BindException - error : {}", error.getDefaultMessage());
                sb.append("%s : %s".formatted(fieldError.getField(),fieldError.getDefaultMessage()));
                sb.append("<br/>");
            }
        });

        model.addAttribute("statusCode", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("referer", referer);
        model.addAttribute("errorMessage", sb.toString());
        return "error/common-error";
    }

    @ExceptionHandler(Throwable.class)
    public String exceptionHandler(Exception e, HttpServletRequest httpServletRequest, Model model) {
        log.error(e.getMessage(), e);
        String referer = httpServletRequest.getHeader("Referer");
        model.addAttribute("referer", referer);
        model.addAttribute("errorMessage", e.getMessage());
        return "error/common-error";
    }

}
