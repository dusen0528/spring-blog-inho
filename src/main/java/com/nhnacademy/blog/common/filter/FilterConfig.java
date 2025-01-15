package com.nhnacademy.blog.common.filter;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogincheckFilter());
        filterRegistrationBean.addUrlPatterns("/member/myinfo.do","/member/logout.do");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }

}
