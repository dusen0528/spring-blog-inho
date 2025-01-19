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
@Import({LoginRequestValidator.class})
class LoginControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    MemberService memberService;

    @BeforeEach
    void setUp() {

    }
    
    @Test
    @DisplayName("로그인 안된 상태에서 로그인페이지 접속")
    void login_form() throws Exception {
        mockMvc.perform(get("/auth/login.do"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인된 상태에서 로그인 페이지에 접근")
    void login_form_when_loggined() throws Exception {
        LoginMember loginMember = LoginMember.of(
                1l,
                "marco@nhnacademy.com",
                "마르코"
        );

        mockMvc.perform(
                    get("/auth/login.do")
                            .sessionAttr("loginMember",loginMember)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/index.do"));
    }

    @Test
    void loginAction_success() throws Exception {

        LoginRequest loginRequest = new LoginRequest(
                "marco@nhnacademy.com",
                "test1234Q!"
        );

        LoginMember loginMember = LoginMember.of(
                1l,
                "marco@nhnacademy.com",
                "마르코"
        );

        when(memberService.doLogin(any(), any())).thenReturn(loginMember);

        mockMvc.perform(
                    post("/auth/loginAction.do")
                            .formField("mbEmail", "marco@nhnacademy.com")
                            .formField("mbPassword", "test1234Q!")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/index.do"))
                .andExpect(request().sessionAttribute("loginMember",loginMember))
                .andDo(print());
    }

    @Test
    @DisplayName("validation-fail : email")
    void loginAction_fail_case1() throws Exception {

        mockMvc.perform(
                        post("/auth/loginAction.do")
                                .formField("mbEmail", "marco-nhnacademy###com")
                                .formField("mbPassword", "test1234Q!")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("error/common-error"))
                .andDo(print());
    }

    @Test
    @DisplayName("validation-fail : password")
    void loginAction_fail_case2() throws Exception {

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
    @DisplayName("login-fail")
    void loginAction_fail_case3() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                "marco@nhnacademy.com",
                "test1234Q!"
        );

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
    @DisplayName("로그인되어 있는 상태에서 logout - success")
    void logoutAction() throws Exception {
        LoginMember loginMember = LoginMember.of(
                1l,
                "marco@nhnacademy.com",
                "마르코"
        );

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("loginMember",loginMember);

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
    @DisplayName("session이 존재하지 않은 상태에서 로그아웃 - fail = redirect")
    void logoutAction_fail_case1() throws Exception {

        mockMvc.perform(
                        post("/auth/logoutAction.do")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(view().name("error/common-error"))
                .andDo(print());
    }

}