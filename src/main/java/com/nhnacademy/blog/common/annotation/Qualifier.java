package com.nhnacademy.blog.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**TODO#2-5 DI 구현시 주입하려는 Bean을 beanName으로 식별 합니다.
 * @Qualifier("doSomething")
 * ex)beanName이 doSomething인 객체를 Context로 부터 식별 합니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Qualifier {
    String value();
}