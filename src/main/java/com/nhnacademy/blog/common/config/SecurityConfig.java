package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.common.security.handler.CustomAuthenticationSuccessHandler;
import com.nhnacademy.blog.common.security.userdetail.MemberDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    /**
     * Spring Security에서 제공하는 loginForm을 사용하지 않고 직접 구현한 login-from : /auth/login.do 사용해서 로그인을 구현 합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorizeRequests ->{
                    authorizeRequests
                            .requestMatchers(
                                    "/",
                                    "/resources/**",
                                    "/favicon.ico",
                                    "/index.do",
                                    "/member/register.do",
                                    "/blog/*/**"
                            ).permitAll()
                            .anyRequest().authenticated(); //나머지 요청은 인증이 필요
                })

                .formLogin(form->{
                    form
                            .loginPage("/auth/login.do")
                            .loginProcessingUrl("/auth/loginAction.do")
                            .successHandler(customAuthenticationSuccessHandler)
                            .usernameParameter("mbEmail")
                            .passwordParameter("mbPassword")
                            .permitAll();
                }).logout(logout ->{
                    logout

                            .logoutUrl("/auth/logoutAction.do")
                            .deleteCookies("JSESSIONID")
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .logoutSuccessUrl("/index.do");

                }).sessionManagement(sessionManagement ->{
                    sessionManagement.maximumSessions(1);
                    sessionManagement.sessionFixation().migrateSession();

                    /**
                     * 기본값을 설정 합니다.
                     * IF_REQUIRED: 필요할 때만 세션을 생성(기본값).
                     * ALWAYS: 항상 세션을 생성.
                     * NEVER: 세션을 절대 생성하지 않음.
                     * STATELESS: 세션을 사용하지 않음 (주로 REST API에서 사용).
                     */
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
                })
                ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(MemberDetailService memberDetailService,PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(memberDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
