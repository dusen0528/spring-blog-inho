package com.nhnacademy.blog.common.annotation.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO#2-2 데이터 처리를 직접 수행하고 예외 처리를 통해 데이터베이스 관련 예외를 반환합니다.
 */
@Component
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Repository {
    String value() default "";
}