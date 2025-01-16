package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.common.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC에서 WebMvcConfigurer는 웹 애플리케이션의 MVC 구성 요소를 커스터마이징하기 위한 인터페이스입니다.
 * 이를 구현하면 Spring MVC의 기본 설정을 확장하거나 사용자 정의 로직을 추가할 수 있습니다.
 * WebMvcConfigurer는 Spring Boot와 Spring Framework의 자동 설정을 변경하거나 추가 설정을 적용하는 데 사용됩니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new LoginCheckInterceptor())
                .addPathPatterns("/member/myinfo.do","/member/logout.do")
                .excludePathPatterns("/index.do");
    }

}
