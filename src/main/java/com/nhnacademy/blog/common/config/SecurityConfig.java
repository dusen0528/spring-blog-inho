package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.common.config.security.userdetail.MemberDetailService;
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
public class SecurityConfig {

    /**
     * TODO#4 - Spring Security에서 제공하는 loginForm을 사용하지 않고 직접 구현한 login-from : /auth/login.do 사용해서 로그인을 구현 합니다.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(authorizeRequests ->{
                    authorizeRequests
                    /**
                     * 로그인(인증) 없이 접근할 수 있도록 경로를 지정 합니다.
                     *  static resource : /js/**, /css/**, /favicon.ico
                     *  /index.do
                     *  /member/register.do
                     *  / <-- 시작 페이지
                     */
                            .requestMatchers("/","/js/**","/css/**","/favicon.ico","/index.do","/member/register.do").permitAll()
                            .anyRequest().authenticated(); //나머지 요청은 인증이 필요
                })

                .formLogin(form->{
                    form
                            //login-form page 경로 설정 : /auth/login.do
                            .loginPage("/auth/login.do")

                            //login processing url 설정 : /auth/loginAction.do <-- 해당 경로는 Spring Security에서 제공하는 로그인 경로를 지정 한 것 입니다. form action을 해당 경로로 지정하세요.
                            .loginProcessingUrl("/auth/loginAction.do")
                    /**
                     * login 성공하면 이동될 경로 지정 : /index.do , alwysUse 값은 false로 지정 합니다.
                     *  - defaultSuccessUrl("/index.do", true):
                     *로그인 성공 후 무조건 지정된 URL인 /index.do로 리다이렉트됩니다.
                     * true 값을 설정하면, Spring Security는 로그인 성공 후에 사용자가 이전에 접근하려 했던 URL을 무시하고 무조건 /index.do로 이동시킵니다.
                     * - defaultSuccessUrl("/index.do", false):
                     * 로그인 성공 후 이전 요청 URL이 있다면 그 페이지로 리다이렉트합니다.
                     * 만약 사용자가 로그인 전에 보호된 리소스나 페이지(/private 등)에 접근하려 했으나 인증되지 않은 상태였다면, 로그인 후에는 그 원래의 페이지로 리다이렉트됩니다.
                     * 만약 로그인 요청 전에 사용자가 특정 페이지를 요청하지 않았다면, 그때는 기본적으로 /index.do로 리다이렉트됩니다.
                     */
                            .defaultSuccessUrl("/index.do",false)
                            //TODO#4-1 login-form에서 사용자의 아이디를 식별할  parameter-name을 지정 : mbEmail 지정 합니다.
                            .usernameParameter("mbEmail")
                            //TODO#4-2 login-form에서 사용자의 패스워드를 식별할 parameter-name을 지정 : mbPassword
                            .passwordParameter("mbPassword")
                            .permitAll();
                }).logout(logout ->{
                    logout
                            //로그아웃 경로를 지정  : /auth/logoutAction.do <-- 해당경론느 controller의 경로가 아닙니다. Security에서 지원하는 logout에 대한 경로를 지정 합니다. 로그아웃시 해당 경로를 호출합니다.
                            .logoutUrl("/auth/logoutAction.do")
                            //TODO#4-3 cookie 삭제
                            .deleteCookies("JSESSIONID")
                            //TODO#4-4  이 설정이 true일 경우, 사용자가 로그아웃할 때 서버의 HttpSession 객체가 무효화됩니다. 즉, 세션에 저장된 모든 데이터가 삭제되고, 새 세션이 생성됩니다.
                            .invalidateHttpSession(true)
                            //TODO#4-5 clearAuthentication(true)는 사용자가 로그아웃할 때 인증 정보를 제거하는 설정입니다. 이 옵션을 true로 설정하면, Spring Security는 사용자가 로그아웃할 때 SecurityContext에 저장된 인증 정보를 완전히 지웁니다.
                            .clearAuthentication(true)
                            //TODO#4-6 logout이 성공하면 이동할 경로 지정 : /index.do
                            .logoutSuccessUrl("/index.do");

                }).sessionManagement(sessionManagement ->{

                    //TODO#4-7 최대 세션 수(종복 로그인 방지)
                    sessionManagement.maximumSessions(1);
                    //TODO#4-8 session 고정 공격 방지:로그인 후 새로운 세션을 생성하고, 기존 세션에서 데이터를 새 세션으로 이전합니다. 이를 통해 세션 고정 공격을 방지합니다.
                    sessionManagement.sessionFixation().migrateSession();

                    /**
                     * TODO#4-9 : 기본값을 설정 합니다.
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

    /**
     * TODO#4-10 - 이전 예제에서 사용하던 inMemory 기반의 UserDetils는 더이상 사용하지 않습니다.
     * - 데이터 베이스에서 회원을 조회하는 방식으로(가장 일반적으로 사용하는 방식) 변경할 예정입니다.
     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        /**
//         * 사용자를 식별할 객체를 생성 합니다.
//         * UserDetails는 Spring Security에서 사용자 인증 정보를 담는 인터페이스입니다. 이 인터페이스는 Spring Security가 사용자의 인증 정보를 관리하고, 이를 기반으로 인증 및 권한 부여를 처리하는 데 사용됩니다.
//         * @see User 는 Spring에서 기본으로 제공하는 UserDetails 인터페이스를 구현한 구현체 입니다.
//         */
//        UserDetails userDetails = User.withDefaultPasswordEncoder()
//                //이메일 설정
//                .username("marco@nhnacademy.com")
//                //비밀번호 설정
//                .password("nhnacademy")
//                //권한 설정 : MEMBER
//                .roles("MEMBER")
//                .build();
//
//        /**
//         *  InMemoryUserDetailsManager객체를 통해서 InMemory기반으로 회원의 정보가 관리될 수 있도록 생성 후 반환 합니다.
//         *  - inMemory 기반으로 보통 테스트 용도에 사용합니다. production 환경에는 사용하지 않습니다.
//         */
//
//        return new InMemoryUserDetailsManager(userDetails);
//    }

    @Bean
    public AuthenticationManager authenticationManager(MemberDetailService memberDetailService,PasswordEncoder passwordEncoder) {
        /**
         * TODO#4-11 - 사용자 인증을 처리하는 `AuthenticationManager` Bean을 생성합니다.
         * - `DaoAuthenticationProvider`를 사용하여 사용자 정보를 데이터베이스에서 조회하고, 비밀번호가 일치하는지 확인하여 인증을 처리합니다.
         *
         * DaoAuthenticationProvider:
         * - `DaoAuthenticationProvider`는 `UserDetailsService`와 `PasswordEncoder`를 사용하여 사용자 인증을 수행하는 기본 제공 인증 제공자입니다.
         * - `UserDetailsService`는 데이터베이스에서 사용자의 세부 정보를 로드하는 인터페이스입니다.
         * - `PasswordEncoder`는 입력된 비밀번호가 저장된 비밀번호와 일치하는지 확인하는 역할을 합니다.
         *
         * AuthenticationManager:
         * - `AuthenticationManager`는 인증을 수행하는 핵심 인터페이스입니다. 여러 인증 제공자(`AuthenticationProvider`)들을 조합하여 인증을 처리합니다.
         * - `ProviderManager`는 여러 `AuthenticationProvider`를 사용하여 인증을 처리할 수 있도록 해줍니다.
         * - 이 경우 `DaoAuthenticationProvider`를 사용하여 데이터베이스에서 사용자 정보를 조회하고, 비밀번호 검증을 진행합니다.
         *
         * 전체 흐름:
         * 1. `DaoAuthenticationProvider`는 `MemberDetailService`에서 사용자의 정보를 조회하고,
         * 2. `passwordEncoder`를 사용하여 입력된 비밀번호와 데이터베이스에 저장된 비밀번호가 일치하는지 검증합니다.
         * 3. 일치하면 인증이 성공하고, 인증 객체(`Authentication`)가 반환됩니다.
         *
         *  Spring Security에서 사용자 인증을 처리하기 위한 `AuthenticationManager`를 설정하는 역할을 합니다.
         */

        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(memberDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        /**
         * bcryptEncoder를  반환 합니다.
         * PasswordEncoderFactories.createDelegatingPasswordEncoder()는 여러 암호화 알고리즘을 지원하는 PasswordEncoder를 생성하며,
         * Spring Security에서 다양한 방식으로 암호화를 처리하고 호환성을 유지할 수 있도록 도와줍니다.
         * 기본적으로 bcrypt를 사용하지만, 필요에 따라 다른 암호화 방식으로 쉽게 전환할 수 있습니다.
         *
         * BCryptPasswordEncoder 명시적으로 사용하기위해서는 다음과 같이 직접 생성할 수 있습니다.
         *  - return new BCryptPasswordEncoder()
         *
         * 직접 사용하기 보다는 다음과 같이 사용하세요
         * - return PasswordEncoderFactories.createDelegatingPasswordEncoder()
         *
         * 장점
         * - PasswordEncoderFactories.createDelegatingPasswordEncoder()에 의해서 암호화된 비밀번호는 앞에 bcrypt 식별자를 붙입니다.
         * - 예를 들어, 암호화된 비밀번호가 bcrypt$2a$10$NPe7A5d7ZJtRY8zR...와 같이 보일 수 있습니다.
         * - 사용자가 로그인할 때, matches() 메서드는 bcrypt 식별자를 확인하고, 해당 알고리즘을 사용하여 암호를 검증합니다.
         * - 기본적으로 DelegatingPasswordEncoder는 최소 하나의 알고리즘을 선택적으로 사용할 수 있도록 설계되었습니다.
         * - Spring Security는 시간이 지나면서 새로운 암호화 알고리즘을 지원하기 위해 delegatingPasswordEncoder를 사용합니다
         */

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
