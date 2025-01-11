package com.nhnacademy.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * TODO#20 Springboot Application의 main class 입니다.
 * - 04. @SpringBootApplication 교재를 참고 합니다.
 */
@SpringBootApplication
public class BlogApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(BlogApplication.class, args);
    }
}