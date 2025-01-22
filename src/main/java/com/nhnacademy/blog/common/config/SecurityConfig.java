package com.nhnacademy.blog.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * TODO#2 - Spring Security Configuration
 * @EnableWebSecurity(debug = true)는 Spring Security의 보안 설정을 디버깅하는 데 유용한 기능을 제공합니다.
 * 이를 통해 Spring Security의 내부 동작을 추적하고, 인증, 권한 부여, 세션 관리 등의 과정을 로그에서 자세히 확인할 수 있습니다.
 * 개발 중에는 문제 해결에 도움이 되지만, 프로덕션 환경에서는 성능이나 보안 측면에서 debug = true를 사용하지 않는 것이 좋습니다.
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

}
