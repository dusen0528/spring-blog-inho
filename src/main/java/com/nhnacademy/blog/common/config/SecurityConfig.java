package com.nhnacademy.blog.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // 이 클래스가 Spring Security 설정을 위한 구성 클래스임을 나타냅니다.
@EnableWebSecurity(debug = true)  // Spring Security의 웹 보안을 활성화하고, 디버깅 정보를 출력하도록 설정합니다.
public class SecurityConfig {

    // HttpSecurity 객체를 이용해 보안 설정을 정의하는 메서드
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // HTTP 요청에 대한 보안 설정
        http
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()) // 모든 요청에 대해 인증을 요구합니다.
                .httpBasic(Customizer.withDefaults())  // HTTP Basic 인증을 사용합니다.
                .formLogin(Customizer.withDefaults()); // 폼 로그인 기능을 사용합니다.

        // 설정이 끝나면 SecurityFilterChain 객체를 반환하여 보안 구성을 완료합니다.
        return http.build();
    }

    // UserDetailsService 빈을 정의하여 사용자 인증 정보를 제공합니다.
    @Bean
    public UserDetailsService userDetailsService() {
        // "marco"라는 사용자 정보를 정의하고, 기본 비밀번호 인코더를 사용하여 암호화합니다.
        UserDetails userDetails = User.withDefaultPasswordEncoder()  // 기본 비밀번호 인코더 사용
                .username("marco")  // 사용자 이름
                .password("12345")  // 비밀번호
                .roles("MEMBER")  // 사용자 역할 (MEMBER)
                .build();  // UserDetails 객체 생성

        // InMemoryUserDetailsManager를 사용하여 메모리 내에서 사용자 정보를 관리합니다.
        return new InMemoryUserDetailsManager(userDetails);
    }
}
