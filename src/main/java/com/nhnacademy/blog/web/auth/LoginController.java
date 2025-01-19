package com.nhnacademy.blog.web.auth;

import com.nhnacademy.blog.common.security.exception.UnauthorizedException;
import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.dto.LoginRequest;
import com.nhnacademy.blog.member.dto.validator.LoginRequestValidator;
import com.nhnacademy.blog.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.Objects;

/**
 * /member/login.do, /member/loginAction.do, /member/logoutAction.do 경로를
 * /auth/login.do , /auth/loginAction.do, /auth/logoutAction.do 로 변경 합니다.
 * - 인증 관련된 method를 /member/~ 로 부터 분리 합니다.
 */

@Slf4j
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final LoginRequestValidator loginRequestValidator;

    @GetMapping("/login.do")
    public String login(Model model, @SessionAttribute(required = false) LoginMember loginMember) {
        if (Objects.nonNull(loginMember)) {
            return "redirect:/index.do";
        }
        return "auth/login";
    }

    @PostMapping("/loginAction.do")
    public String loginAction(LoginRequest loginRequest, BindingResult bindingResult, HttpSession session) {

        // loginRequestValidator.validate() 메서드를 호출해서 검증합니다. 검증된 결과는 bindingResult에 반영 됨니다.
        loginRequestValidator.validate(loginRequest, bindingResult);

        /**
         * 나머지 로직은 @Valid or @validated  사용했을 때 로직과 동일하게 처리 하면 됨니다.
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

        //추가코드
        if (Objects.isNull(loginMember)) {
            throw new UnauthorizedException();
        }

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

}
