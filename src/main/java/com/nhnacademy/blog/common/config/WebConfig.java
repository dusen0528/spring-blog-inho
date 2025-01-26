package com.nhnacademy.blog.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfigurer
 * Spring MVC에서 WebMvcConfigurer는 웹 애플리케이션의 MVC 구성 요소를 커스터마이징하기 위한 인터페이스입니다.
 * 이를 구현하면 Spring MVC의 기본 설정을 확장하거나 사용자 정의 로직을 추가할 수 있습니다.
 * WebMvcConfigurer는 Spring Boot와 Spring Framework의 자동 설정을 변경하거나 추가 설정을 적용하는 데 사용됩니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     *  controller 구현 없이 간단히 view로 연결할 수 있습니다.
     *  - /  접근하면 -> /index.do redirect 합니다.
     *  - 정적으로 페이지를 보여줘야 한다면 controller 생성없이 다음과 같이 설정할 수 있습니다.
     *    registry.addViewController("/").setViewName("forward:/index.do");
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //TODO#1-4  '/' <- 요청이 발생하면 -> /index.do redirect 합니다. 아래 urlPath, redirectUrl 변경하세요.
        registry.addRedirectViewController("/", "/index.do");

        /**
         * TODO#1-5 login page mapping
         * - 간단한 view 전용 페이지는 별도의 Controller 설정없이 가볍게 맵핑 할 수 있습니다.
         *  registry.addViewController() 사용해서 /auth/login.do 의 요청의 viewName : auth/login 설정 합니다.
         */
        registry.addViewController("/auth/login.do").setViewName("auth/login");

    }

}