package com.nhnacademy.blog.web.auth;

import com.nhnacademy.blog.member.auth.LoginMember;
import com.nhnacademy.blog.member.dto.LoginRequest;
import com.nhnacademy.blog.member.dto.validator.LoginRequestValidator;
import com.nhnacademy.blog.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {LoginController.class})
//TODO#1-1 - LoginRequestValidator import 합니다.
@Import({LoginRequestValidator.class})
class LoginControllerTest {

    /**
     * TODO#1-2 - mockMvc field 주입합니다.
     * MockMvc는 Spring Test 모듈에서 제공하는 클래스이며, Spring MVC 애플리케이션의 웹 계층을 테스트할 때 사용됩니다.
     * MockMvc를 사용하면 실제 HTTP 요청을 서버에 보내지 않고도 컨트롤러와 요청-응답을 테스트할 수 있습니다.
     * 즉, 웹 계층 테스트(단위 테스트)를 쉽게 할 수 있는 도구입니다.
     */
    @Autowired
    MockMvc mockMvc;

    /**
     * spring-test에서는 더 이상 mockBean을 사용하지 않습니다.
     * memberService는 LoginController에 주입됩니다.
     * TODO#1-3 - @MockitoBean을 선언합니다.
     */
    @MockitoBean
    MemberService memberService;

    @BeforeEach
    void setUp() {
        // 테스트 전 초기화가 필요한 경우 이곳에 작성합니다.
    }

    @Test
    @DisplayName("로그인되지 않은 상태에서 로그인 페이지 접속")
    void login_form() throws Exception {
        /**
         * TODO#1-4 - mockMvc를 이용한 login.do 검증 예제입니다.
         * get() -> GET 메서드를 의미합니다.
         * post(), put(), delete() 등 HTTP 요청 방식에 따라 다릅니다.
         * 즉, /auth/login.do에 GET 요청을 보낸다는 의미입니다.
         */
        mockMvc.perform(get("/auth/login.do"))
                // HTTP 상태 코드 200(OK)을 검증합니다.
                .andExpect(status().isOk())
                // viewName이 auth/login임을 검증합니다.
                .andExpect(view().name("auth/login"))
                // print()는 전송되는 메시지를 로그로 확인할 수 있습니다. 테스트할 때 반드시 사용하세요.
                .andDo(print());
    }

    @Test
    @DisplayName("로그인된 상태에서 로그인 페이지에 접근")
    void login_form_when_logged_in() throws Exception {
        LoginMember loginMember = LoginMember.of(
                1L,
                "marco@nhnacademy.com",
                "마르코"
        );
        /**
         * TODO#1-5 - /auth/login.do GET 요청을 보냅니다.
         * sessionAttr()을 이용해 loginMember가 세션에 등록되어 있는 환경을 구성합니다.
         */
        mockMvc.perform(
                        get("/auth/login.do")
                                .sessionAttr("loginMember", loginMember)
                )
                // 이미 로그인되어 있으므로 리디렉션됩니다. 3xx 응답 코드를 검증합니다.
                .andExpect(status().is3xxRedirection())
                // viewName이 "redirect:/index.do"임을 검증합니다.
                .andExpect(view().name("redirect:/index.do"));
    }

    @Test
    @DisplayName("로그인 - 성공")
    void loginAction_success() throws Exception {

        LoginRequest loginRequest = new LoginRequest(
                "marco@nhnacademy.com",
                "test1234Q!"
        );

        LoginMember loginMember = LoginMember.of(
                1L,
                "marco@nhnacademy.com",
                "마르코"
        );

        //TODO#1-6 memberService.doLogin을 호출하면 loginMember가 응답됩니다.
        when(memberService.doLogin(any(), any())).thenReturn(loginMember);

        mockMvc.perform(
                        // "/auth/loginAction.do"에 POST 요청을 보냅니다.
                        post("/auth/loginAction.do")
                                // form 필드 mbEmail, mbPassword를 body에 담아서 전송합니다. 적당한 값을 설정하세요.
                                .formField("mbEmail", "marco@nhnacademy.com")
                                .formField("mbPassword", "test1234Q!")
                )
                // 로그인 성공 시 리디렉션됩니다. 3xx HTTP 상태 코드 및 viewName을 검증합니다.
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/index.do"))
                // 세션에 loginMember 객체가 실제로 존재하는지 검증합니다.
                .andExpect(request().sessionAttribute("loginMember", loginMember))
                .andDo(print());
    }

    @Test
    @DisplayName("validation 실패 - 이메일 형식 오류")
    void loginAction_fail_case1() throws Exception {
        /**
         * TODO#1-7 - 로그인 실패 케이스 - 이메일 형식이 잘못된 경우.
         * post()와 formField() 등을 이용하여 구현합니다.
         */
        mockMvc.perform(
                        post("/auth/loginAction.do")
                                .formField("mbEmail", "marco-nhnacademy###com")
                                .formField("mbPassword", "test1234Q!")
                )
                // 로그인에 실패하면 HTTP 상태 코드 401을 응답합니다. 회원에게 사유를 상세히 알릴 필요는 없습니다.
                .andExpect(status().isUnauthorized())
                // viewName을 검증합니다.
                .andExpect(view().name("error/common-error"))
                .andDo(print());
    }

    @Test
    @DisplayName("validation 실패 - 비밀번호 오류")
    void loginAction_fail_case2() throws Exception {
        /**
         * TODO#1-8 - 비밀번호 검증 실패 테스트 케이스를 구현합니다.
         * - 비밀번호가 null이거나 공백일 경우
         */
        mockMvc.perform(
                        post("/auth/loginAction.do")
                                .formField("mbEmail", "marco@nhnacademy.com")
                                .formField("mbPassword", "")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("error/common-error"))
                .andDo(print());
    }

    @Test
    @DisplayName("아이디/비밀번호 불일치로 인한 로그인 실패")
    void loginAction_fail_case3() throws Exception {
        /**
         * TODO#1-9 - 아이디/비밀번호 불일치로 인해 로그인에 실패하는 케이스를 검증합니다.
         */
        when(memberService.doLogin(any(), any())).thenReturn(null);

        mockMvc.perform(
                        post("/auth/loginAction.do")
                                .formField("mbEmail", "marco@nhnacademy.com")
                                .formField("mbPassword", "test1234Q!")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("error/common-error"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인된 상태에서 로그아웃 성공")
    void logoutAction() throws Exception {
        LoginMember loginMember = LoginMember.of(
                1L,
                "marco@nhnacademy.com",
                "마르코"
        );
        /**
         * TODO#1-10 로그인된 상태에서 (세션에 loginMember가 존재하는) /auth/logoutAction.do 페이지로 로그인을 시도할 때
         * - /index.do로 리디렉션되는지 검증하는 코드를 작성합니다.
         */
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginMember", loginMember);

        mockMvc.perform(
                        post("/auth/logoutAction.do")
                                .session(session)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/index.do"))
                .andExpect(request().sessionAttributeDoesNotExist("loginMember"))
                .andDo(print());
    }

    @Test
    @DisplayName("세션이 존재하지 않는 상태에서 로그아웃 - 실패")
    void logoutAction_fail_case1() throws Exception {
        /**
         * TODO#1-11 - 세션이 존재하지 않는 상태에서 로그아웃을 시도할 경우 검증하는 테스트 케이스를 작성합니다.
         */
        mockMvc.perform(
                        post("/auth/logoutAction.do")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("error/common-error"))
                .andDo(print());
    }
}
