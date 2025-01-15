package com.nhnacademy.blog.web.member;

import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.dto.LoginRequest;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String registerAction(MemberRegisterRequest memberRegisterRequest, RedirectAttributes redirectAttributes) {
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

    @GetMapping("/login.do")
    public String login(Model model,@SessionAttribute(required = false) LoginMember loginMember) {
        if (Objects.nonNull(loginMember)) {
            return "redirect:/index.do";
        }
        return "member/login";
    }

    @PostMapping("/loginAction.do")
    public String loginAction(LoginRequest loginRequest, HttpSession session) {

        LoginMember loginMember = memberService.doLogin(loginRequest.getMbEmail(), loginRequest.getMbPassword());
        log.debug("loginAction: {}", loginMember);
        session.setAttribute("loginMember", loginMember);
        //30분 session 유지
        session.setMaxInactiveInterval(60*30);

        return "redirect:/index.do";
    }

    @PostMapping("/logoutAction.do")
    public String logoutAction(HttpSession session,@SessionAttribute(required = false) LoginMember loginMember) {
        if(Objects.isNull(loginMember)) {
            throw new UnauthorizedException();
        }
        session.invalidate();
        return "redirect:/index.do";
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

    @ExceptionHandler(Throwable.class)
    public String exceptionHandler(Exception e, HttpServletRequest httpServletRequest, Model model) {
        log.error(e.getMessage(), e);
        String referer = httpServletRequest.getHeader("Referer");

        model.addAttribute("referer", referer);
        model.addAttribute("errorMessage", e.getMessage());
        return "error/common-error";
    }

}
