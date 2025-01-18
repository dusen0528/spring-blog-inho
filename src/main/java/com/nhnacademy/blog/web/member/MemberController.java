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

    //TODO#3 - Component로 등록된 loginRequestValidator를 주입 합니다.
    private final LoginRequestValidator loginRequestValidator;

    @RequestMapping(value = "/register.do")
    public String register() {
        return "member/register";
    }

    @PostMapping(value = "/registerAction.do")
    public String registerAction(@Validated MemberRegisterRequest memberRegisterRequest,/*TODO#4-2 이번에는 bindingResult를 사용하지 않습니다.*/ RedirectAttributes redirectAttributes) {
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

    /**
     * TODO#4 - LoginRequestValidator적용
     * @Valid or @Validated는 사용하지 않습니다.
     */
    @PostMapping("/loginAction.do")
    public String loginAction(LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        // TODO#4-1 - loginRequestValidator.validate() 메서드를 호출해서 검증합니다. 검증된 결과는 bindingResult에 반영 됨니다.
        loginRequestValidator.validate(loginRequest, bindingResult);

        /**
         * TODO#4-2 나머지 로직은 @Valid or @validated  사용했을 때 로직과 동일하게 처리 하면 됨니다.
         * - 실제로 적용이 되었는지 로그인 페이지에 접속하여 테스트 합니다.
         * loginRequest dto  검증 합니다.
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
