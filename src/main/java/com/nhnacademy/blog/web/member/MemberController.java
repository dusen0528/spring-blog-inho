package com.nhnacademy.blog.web.member;

import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.dto.LoginRequest;
import com.nhnacademy.blog.member.dto.MemberRegisterRequest;
import com.nhnacademy.blog.member.dto.MemberResponse;
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

    /**
     * TODO#4 - MemberRegisterRequest validation 합니다.
     * '@Valid' or '@Validated' annotation을 사용할 수 있습니다.
     * @see jakarta.validation.Valid
     * @see org.springframework.validation.annotation.Validated
     */
    @PostMapping(value = "/registerAction.do")
    public String registerAction(/*TODO#4-1 @Validated를 선언 합니다.*/@Validated MemberRegisterRequest memberRegisterRequest,/*TODO#4-2 이번에는 bindingResult를 사용하지 않습니다.*/ RedirectAttributes redirectAttributes) {
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
    public String loginAction(/* TODO#2-1 검증한 dto 앞에 @Valid or @Validated 선언 합니다. */@Valid LoginRequest loginRequest,/* TODO#2-2 검증을한 dto 바로 뒤에 BindingResult를 선언 합니다. */ BindingResult bindingResult, HttpSession session) {

        /**
         * TODO#2-3 loginRequest dto  검증 합니다.
         * bindingResult.hasErrors() 호출하면 error가 발생 했다면 true를 반환 합니다.
         */
        if(bindingResult.hasErrors()) {
            //bindingResult.getAllErrors() 메서드를 이용해서 모든 message 출력 합니다.
            // 반드시 로그인 수행 후 로그를 확인 합니다.
            bindingResult.getAllErrors().forEach(error ->{
                    log.error("LoginRequest - error : {}", error.getDefaultMessage());
                }
            );
            // UnauthorizedException 예외 발생
            throw new UnauthorizedException();
        }

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

    /**
     * TODO#5 - bbindExceptionHandler 구현
     * @ResponseStatus() 선언 합니다.400 BAD_REQUEST를 응답 합니다.
     * @valid or @Validated annotation에 의해서 발생한 예외는 BindExcetpion.class 로 핸들링 할 수 있습니다.
     * @ExceptionHandler() 메서드를 이용해서 BindException.class를 catch 합니다.
     *
     */
    @ResponseStatus(code=HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public String bindExceptionHandler(BindException e, HttpServletRequest httpServletRequest, Model model) {
        log.error(e.getMessage(), e);
        String referer = httpServletRequest.getHeader("Referer");

        model.addAttribute("referer", referer);
        StringBuilder sb  = new StringBuilder();

        //sb에 담길 error message를 작성하세요.
        e.getBindingResult().getAllErrors().forEach(error ->{
            //java 21에서 새롭게 추가 되었습니다. 새로변 변경된 instanceoof 에 대해서 공부해보세요
            //[참고] - https://www.baeldung.com/java-lts-21-new-features
            if(error instanceof FieldError fieldError){

                //TODO#5-1 sb.append이용해서 message를 buffer에 저장 합니다.
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

    /**
     * TODO#2-4 UnauthorizedException handler 구현
     *  - 아래 exceptionHandler()를 참고해서 구현 합니다.
     *  - http response status code = 401 , 응답 될 수 있도록   @ResponseStatus 사용 합니다.
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

    @ExceptionHandler(Throwable.class)
    public String exceptionHandler(Exception e, HttpServletRequest httpServletRequest, Model model) {
        log.error(e.getMessage(), e);
        String referer = httpServletRequest.getHeader("Referer");
        model.addAttribute("referer", referer);
        model.addAttribute("errorMessage", e.getMessage());
        return "error/common-error";
    }

}
