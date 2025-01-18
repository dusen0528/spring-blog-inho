package com.nhnacademy.blog.web.member;

import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.dto.LoginRequest;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.dto.validator.LoginRequestValidator;
import com.nhnacademy.blog.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @RequestMapping(value = "/register.do")
    public String register() {
        return "member/register";
    }

    @PostMapping(value = "/registerAction.do")
    public String registerAction(@Validated MemberRegisterRequest memberRegisterRequest, RedirectAttributes redirectAttributes) {
        log.debug("memberRegisterReques1t: {}", memberRegisterRequest);
        memberService.registerMember(memberRegisterRequest);
        redirectAttributes.addFlashAttribute("memberRegisterRequest", memberRegisterRequest);
        return "redirect:/member/registerResult.do";
    }

    @GetMapping(value = "/registerResult.do")
    public String registerResult(Model model) {
        log.debug("memberRegisterResult2: {}", model.getAttribute("memberRegisterRequest") );
        return "member/registerResult";
    }

    @GetMapping("/myinfo.do")
    public String myinfo(Model model, @SessionAttribute(required = false) LoginMember loginMember) {
        if(Objects.isNull(loginMember)) {
            throw new UnauthorizedException();
        }

        MemberResponse memberResponse = memberService.getMember(loginMember.getMbNo());
        model.addAttribute("memberResponse", memberResponse);
        return "member/myinfo";
    }


/**
    TODO#2 - bindExceptionHandler, exceptionHandler 공통으로 Exception을 핸들링하기 위해서 CommonAdvice로 이동 합니다.
     @see com.nhnacademy.blog.common.advice.CommonAdvice

    @ResponseStatus(code=HttpStatus.BAD_REQUEST)
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

 */

    /**
    TODO#3 UnauthorizedException 로그인 과정에서 AuthController에서 사용하기 위해서 LoginAdvice로 이동 합니다.
     @see com.nhnacademy.blog.common.advice.LoginAdvice

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
     */

}
