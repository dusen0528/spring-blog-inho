package com.nhnacademy.blog.common.config;

import com.nhnacademy.blog.common.filter.LoginCheckFilter;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    /* TODO#4 - 더이상 loginCheck filter를 사용할 필요 없습니다. 모든 인증/인가는 Spring Security가 담당 합니다.
    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());
        filterRegistrationBean.addUrlPatterns("/member/myinfo.do","/auth/logout.do");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
     */

}
