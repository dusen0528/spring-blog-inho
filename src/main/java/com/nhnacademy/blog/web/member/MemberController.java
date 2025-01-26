package com.nhnacademy.blog.web.member;

import com.nhnacademy.blog.common.security.userdetail.MemberDetails;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
import com.nhnacademy.blog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String myinfo(Model model, @AuthenticationPrincipal MemberDetails memberDetails) {
        MemberResponse memberResponse = memberService.getMemberByEmail(memberDetails.getUsername());
        model.addAttribute("memberResponse", memberResponse);
        return "member/myinfo";
    }

}
