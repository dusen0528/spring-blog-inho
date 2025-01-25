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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
    public String myinfo(Model model, @AuthenticationPrincipal User loginMember) {
//        if(Objects.isNull(loginMember)) {
//            throw new UnauthorizedException();
//        }

        MemberResponse memberResponse = memberService.getMemberByEmail(loginMember.getUsername());
        model.addAttribute("memberResponse", memberResponse);
        return "member/myinfo";
    }

}
