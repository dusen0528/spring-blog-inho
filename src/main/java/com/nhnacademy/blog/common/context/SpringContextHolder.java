package com.nhnacademy.blog.common.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContextHolder {
    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.nhnacademy.blog");

    private SpringContextHolder(){
        //SpringContextHolder new ContextHolder() 시도 한다면 IllegalStateException 예외가 발생할 수 있도록 구현 합니다.
        throw new IllegalStateException("ContextHolder should not be instantiated");
    }

    public static synchronized ApplicationContext getApplicationContext() {
        //Context를 반환 합니다.
        return applicationContext;
    }
}
